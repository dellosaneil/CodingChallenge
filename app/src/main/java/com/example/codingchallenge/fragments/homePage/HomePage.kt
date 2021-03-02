package com.example.codingchallenge.fragments.homePage

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.codingchallenge.Constants
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePage : Fragment(), HomePageAdapter.HomePageClickListener {

    private val homePageViewModel : HomePageViewModel by viewModels()
    private lateinit var dataStore : DataStore<Preferences>
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val homePageAdapter : HomePageAdapter by lazy { HomePageAdapter(this) }
    private val TAG = "HomePage"

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
    }

    private fun setUpRecyclerView() {
        binding.homePageRecyclerView.apply{
            adapter = homePageAdapter
            val spanCount = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 5
            layoutManager = GridLayoutManager(requireActivity(), spanCount)
        }
        homePageViewModel.dataList.observe(viewLifecycleOwner){ outer ->
            outer?.let{
                homePageAdapter.insertMovieData(it)
            }
        }
    }

    private fun checkDataForUpdate() {
        val dataStoreKey = booleanPreferencesKey(Constants.CHECK_UPDATED_KEY)
        lifecycleScope.launch(Dispatchers.IO) {
            val preference = dataStore.data.first()
            if(preference[dataStoreKey] == null || preference[dataStoreKey] == false) {
                homePageViewModel.retrieveAllAppleData()
                dataStore.edit {
                    it[dataStoreKey] = true
                }
                homePageViewModel.searchAppleList("")
            }
        }
    }

    private fun initializeDataStore() {
        dataStore = requireContext().createDataStore(Constants.DATA_STORE)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun pageClicked(position: Int) {
        Log.i(TAG, "pageClicked: $position")
    }


}