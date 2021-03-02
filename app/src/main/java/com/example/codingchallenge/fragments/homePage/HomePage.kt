package com.example.codingchallenge.fragments.homePage

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.codingchallenge.Constants
import com.example.codingchallenge.Constants.Companion.LATEST_SEARCH_VIEW
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.FragmentHomePageBinding
import com.example.codingchallenge.room.AppleEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePage : Fragment(), HomePageAdapter.HomePageClickListener, SearchView.OnQueryTextListener {

    private val homePageViewModel: HomePageViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val homePageAdapter: HomePageAdapter by lazy { HomePageAdapter(this) }
    private val TAG = "HomePage"
    private var updatedList = listOf<AppleEntity>()
    private lateinit var checkUpdatedKey: Preferences.Key<Boolean>
    private lateinit var searchViewKey: Preferences.Key<String>

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
        checkDataForUpdate()
        setUpRecyclerView()
        initializeToolbar()
        initializeChipGroup()
    }

    private fun initializeChipGroup() {
        val chipArray = arrayOf(binding.homePageChipAction, binding.homePageChipComedy, binding.homePageChipDrama, binding.homePageChipKids, binding.homePageChipRomance, binding.homePageChipSciFi)
        val labelArray = resources.getStringArray(R.array.chip_array)
        repeat(chipArray.size){
            chipArray[it].homePageChip.text = labelArray[it]
        }

    }

    private fun initializeToolbar() {
        val search = binding.homePageToolbar.menu?.findItem(R.id.homeMenu_search)
        val searchView = search?.actionView as SearchView
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

    private fun checkDataForUpdate() {
        lifecycleScope.launch(IO) {
            val preference = dataStore.data.first()
            if (preference[checkUpdatedKey] == null || preference[checkUpdatedKey] == false) {
                homePageViewModel.retrieveAllAppleData()
                dataStore.edit {
                    it[checkUpdatedKey] = true
                }
                homePageViewModel.searchAppleList("")
            }else{
                preference[searchViewKey]?.let{
                    homePageViewModel.searchAppleList(it)
                }
            }
        }
    }

    private fun initializeDataStore() {
        dataStore = requireContext().createDataStore(Constants.DATA_STORE)
        checkUpdatedKey = booleanPreferencesKey(Constants.CHECK_UPDATED_KEY)
        searchViewKey = stringPreferencesKey(LATEST_SEARCH_VIEW)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun pageClicked(position: Int) {
        val movieDetails = updatedList[position]
        val action = HomePageDirections.homePageDetailsPage(movieDetails)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            homePageViewModel.searchAppleList(it)
            lifecycleScope.launch(IO) {
                dataStore.edit { preference ->
                    preference[searchViewKey] = query
                }
            }
            return true
        }
        return false
    }
}