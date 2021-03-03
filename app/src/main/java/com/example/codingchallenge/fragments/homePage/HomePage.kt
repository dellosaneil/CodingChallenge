package com.example.codingchallenge.fragments.homePage

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.codingchallenge.Constants
import com.example.codingchallenge.Constants.Companion.LATEST_FIELD_KEY
import com.example.codingchallenge.Constants.Companion.LATEST_FILTER_KEY
import com.example.codingchallenge.Constants.Companion.LATEST_SEARCH_VIEW
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.FragmentHomePageBinding
import com.example.codingchallenge.room.AppleEntity
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomePage : Fragment(), HomePageAdapter.HomePageClickListener, SearchView.OnQueryTextListener,
    ChipGroup.OnCheckedChangeListener {

    private val homePageViewModel: HomePageViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val homePageAdapter: HomePageAdapter by lazy { HomePageAdapter(this) }
    private var updatedList = listOf<AppleEntity>()
    private lateinit var checkUpdatedKey: Preferences.Key<Boolean>
    private lateinit var searchViewKey: Preferences.Key<String>
    private lateinit var filterViewKey: Preferences.Key<String>
    private lateinit var latestMovieKey: Preferences.Key<Int>
    private var latestSearch = ""
    private var latestFilter = 0
    private lateinit var filterArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homePageViewModel
        initializeDataStore()
        checkSavedPreferences()
        setUpRecyclerView()
        initializeChipGroup()
    }

    /*Places the labels on the Chips in the ChipGroup*/
    private fun initializeChipGroup() {
        binding.homePageChipGroup.setOnCheckedChangeListener(this)
        val chipArray = arrayOf(
            binding.homePageChipAction,
            binding.homePageChipComedy,
            binding.homePageChipDrama,
            binding.homePageChipKids,
            binding.homePageChipRomance,
            binding.homePageChipSciFi
        )
        val labelArray = resources.getStringArray(R.array.chip_array)
        repeat(chipArray.size) {
            chipArray[it].homePageChip.text = labelArray[it + 1]
        }
    }

    /* Stops the Shimmer Animation, and hides the Shimmer Layout.
    * Shows RecyclerView*/
    private suspend fun hideShimmerShowRecyclerView(){
        withContext(Main) {
            binding.homePageShimmer.apply {
                stopShimmerAnimation()
                visibility = View.GONE
            }
            binding.homePageRecyclerView.visibility = View.VISIBLE
        }
    }
    /* Show and start shimmer animation
    * Hides RecyclerView*/
    private suspend fun showShimmerHideRecyclerView(){
        withContext(Main){
            binding.homePageShimmer.apply {
                startShimmerAnimation()
                visibility = View.VISIBLE
            }
            binding.homePageRecyclerView.visibility = View.GONE
        }
    }


    /*Allows the searchView in the Toolbar to listen to real time query.
    * Put value on the Search View Query if the user previously wrote a query before closing the application*/
    private fun initializeSearchView() {
        val searchMenu = binding.homePageToolbar.menu?.findItem(R.id.homeMenu_search)
        val searchView = searchMenu?.actionView as SearchView
        searchView.apply {
            setIconifiedByDefault(false)
            queryHint = getText(R.string.homePage_searchHint)
            setQuery(latestSearch, true)
        }
        searchView.setOnQueryTextListener(this)
    }

    /* Set up the recyclerview, so that it would be ready to display data in a grid.*/
    private fun setUpRecyclerView() {
        binding.homePageRecyclerView.apply {
            adapter = homePageAdapter
            val spanCount =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 5
            layoutManager = GridLayoutManager(requireActivity(), spanCount)
        }
        homePageViewModel.dataList.observe(viewLifecycleOwner) { outer ->
            outer?.let {
                homePageAdapter.insertMovieData(it)
                updatedList = it
            }
        }
    }

    /*Checks the DataStore preferences stored in the application.*/
    private fun checkSavedPreferences() {
        lifecycleScope.launch(IO) {
            val preference = dataStore.data.first()
            if (preference[checkUpdatedKey] == null || preference[checkUpdatedKey] == false) {
                showShimmerHideRecyclerView()
                if (hasNetworkAvailable()) {
                    getDataFromApi()
                    hideShimmerShowRecyclerView()
                } else {
                    internetCheckSnackBar()
                }
            } else {
                handleReEntry(preference)
            }
        }
    }

    /*This function is called when user already has the data.
    * It would check all preferences and restore the last state of the application.*/

    private suspend fun handleReEntry(preference: Preferences) {
        preference[searchViewKey]?.let {
            latestSearch = it
        }
        preference[filterViewKey]?.let {
            latestFilter = filterArray.indexOf(it)
        }
        homePageViewModel.searchWithFilter(latestSearch, filterArray[latestFilter])
        redirectToPreviousMovie(preference[latestMovieKey]!!)
    }


    /* Only called once when RoomData base is empty.
    * Checked using DataStore Preference.
    * Updates 'checkUpdatedKey' to true in order to stop calling this function in the future*/
    private suspend fun getDataFromApi() {
        homePageViewModel.retrieveAllAppleData()
        dataStore.edit {
            it[checkUpdatedKey] = true
            it[latestMovieKey] = -1
        }
        homePageViewModel.searchWithFilter("", "")
    }

    /*Only called when the user has no RoomData and has no internet connection.
    * Added an action to retry when the user has internet.*/
    private fun internetCheckSnackBar() {
        Snackbar.make(
            requireView(),
            getString(R.string.homePage_snackBarTitle),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(getString(R.string.homePage_snackBarAction)) {
                checkSavedPreferences()
            }
            show()
        }
    }

    /*A simple function to check if the device is connected to the internet.
    * Would only be called when the user newly installed the application and does not have the movie data.*/
    @Suppress("DEPRECATION")
    private fun hasNetworkAvailable(): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context?.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
    }

    /*Redirects the user to the Details Page whenever the DataStoreKey('latestMovieKey') is not -1.
    * Uses Navigation component to redirect to the Details Page.*/
    private suspend fun redirectToPreviousMovie(indexNumber: Int) {
        withContext(Main) {
            if (indexNumber != -1) {
                val movieDetails = updatedList[indexNumber]
                val action = HomePageDirections.homePageDetailsPage(movieDetails)
                Navigation.findNavController(requireView()).navigate(action)
            } else {
                initializeSearchView()
                checkChipButton()
            }
        }
    }

    /*Restores the ChipButton state when the user closed the program.*/
    private fun checkChipButton() {
        when (latestFilter) {
            1 -> binding.homePageChipAction.homePageChip.isChecked = true
            2 -> binding.homePageChipComedy.homePageChip.isChecked = true
            3 -> binding.homePageChipDrama.homePageChip.isChecked = true
            4 -> binding.homePageChipKids.homePageChip.isChecked = true
            5 -> binding.homePageChipRomance.homePageChip.isChecked = true
            6 -> binding.homePageChipSciFi.homePageChip.isChecked = true
        }
    }

    /*A function to initialize all the variables related to DataStore to be used Globally.*/
    private fun initializeDataStore() {
        filterArray = resources.getStringArray(R.array.chip_array)
        dataStore = requireContext().createDataStore(Constants.DATA_STORE)
        checkUpdatedKey = booleanPreferencesKey(Constants.CHECK_UPDATED_KEY)
        searchViewKey = stringPreferencesKey(LATEST_SEARCH_VIEW)
        filterViewKey = stringPreferencesKey(LATEST_FILTER_KEY)
        latestMovieKey = intPreferencesKey(LATEST_FIELD_KEY)
    }

    /*An interface that is attached to the RecyclerView Adapter. It listens to click events in the recycler view then re-direct it to the detail page of the clicked movie.
    * Updates the 'latestMovieKey' to the current position of the movie.*/
    override fun pageClicked(position: Int) {
        val movieDetails = updatedList[position]
        val action = HomePageDirections.homePageDetailsPage(movieDetails)
        Navigation.findNavController(requireView()).navigate(action)
        lifecycleScope.launch(IO) {
            dataStore.edit {
                it[latestMovieKey] = position
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    /*Gets called every time the user searches using the Search View.
    * Searches the database if there is a movie with the same letters on the search view.
    * Updates the 'searchViewKey' every time the function gets called.
    * It will be used to restore the state of the application once it gets closed.*/
    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            lifecycleScope.launch(IO) {
                homePageViewModel.searchWithFilter(it, filterArray[latestFilter])
                latestSearch = it
                dataStore.edit { preference ->
                    preference[searchViewKey] = query
                }
            }
            return true
        }
        return false
    }


    /* Listens to the children of the chipGroup when a Chip is clicked.
    * Searches the database by genre that was clicked.
    * Updates the 'filterViewKey' in order to restore the state of the application once it gets closed.*/
    override fun onCheckedChanged(group: ChipGroup?, checkedId: Int) {
        val filterArray = resources.getStringArray(R.array.chip_array)
        latestFilter = when (checkedId) {
            R.id.homePage_chipAction -> 1
            R.id.homePage_chipComedy -> 2
            R.id.homePage_chipDrama -> 3
            R.id.homePage_chipKids -> 4
            R.id.homePage_chipRomance -> 5
            R.id.homePage_chipSciFi -> 6
            else -> 0
        }
        lifecycleScope.launch(IO) {
            dataStore.edit {
                it[filterViewKey] = filterArray[latestFilter]
                homePageViewModel.searchWithFilter(latestSearch, filterArray[latestFilter])
            }
        }
    }

    /*Removes the reference of the viewBind*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}















