package com.example.codingchallenge.fragments.homePage

import android.content.res.Configuration
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
    private var latestFilter = -1
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
        initializeDataStore()
        checkSavedPreferences()
        setUpRecyclerView()
        initializeChipGroup()
    }


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

    private fun initializeToolbar() {
        val search = binding.homePageToolbar.menu?.findItem(R.id.homeMenu_search)
        val searchView = search?.actionView as SearchView
        searchView.apply {
            setIconifiedByDefault(false)
            queryHint = getText(R.string.homePage_searchHint)
            setQuery(latestSearch, true)
        }
        searchView.setOnQueryTextListener(this)
    }

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

    private fun checkSavedPreferences() {
        lifecycleScope.launch(IO) {
            val preference = dataStore.data.first()
            if (preference[checkUpdatedKey] == null || preference[checkUpdatedKey] == false) {
                homePageViewModel.retrieveAllAppleData()
                dataStore.edit {
                    it[checkUpdatedKey] = true
                    it[latestMovieKey] = -1
                }
                homePageViewModel.searchWithFilter("", "")
            } else {
                preference[searchViewKey]?.let {
                    latestSearch = it
                }
                preference[filterViewKey]?.let {
                    latestFilter = filterArray.indexOf(it)
                }
                homePageViewModel.searchWithFilter(latestSearch, filterArray[latestFilter])
                redirectToPreviousMovie(preference[latestMovieKey]!!)
            }
        }
    }


    private suspend fun redirectToPreviousMovie(indexNumber: Int) {
        withContext(Main) {
            if (indexNumber != -1) {
                val movieDetails = updatedList[indexNumber]
                val action = HomePageDirections.homePageDetailsPage(movieDetails)
                Navigation.findNavController(requireView()).navigate(action)
            }else{
                withContext(Main) {
                    initializeToolbar()
                    checkChipButton()
                }
            }
        }
    }


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


    private fun initializeDataStore() {
        filterArray = resources.getStringArray(R.array.chip_array)
        dataStore = requireContext().createDataStore(Constants.DATA_STORE)
        checkUpdatedKey = booleanPreferencesKey(Constants.CHECK_UPDATED_KEY)
        searchViewKey = stringPreferencesKey(LATEST_SEARCH_VIEW)
        filterViewKey = stringPreferencesKey(LATEST_FILTER_KEY)
        latestMovieKey = intPreferencesKey(LATEST_FIELD_KEY)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
}















