package com.example.codingchallenge.fragments.homePage

import android.os.Bundle
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
import com.example.codingchallenge.Constants
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.FragmentHomePageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomePage : Fragment() {

    private val homePageViewModel : HomePageViewModel by viewModels()
    private lateinit var dataStore : DataStore<Preferences>
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!


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
        binding.button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_detailsPage)
        }
    }


    private fun checkDataForUpdate() {
        val dataStoreKey = booleanPreferencesKey(Constants.CHECK_UPDATED_KEY)
        lifecycleScope.launch(Dispatchers.IO) {
            val preference = dataStore.data.first()
            if(preference[dataStoreKey] == null || preference[dataStoreKey] == false){
                homePageViewModel.retrieveAllAppleData()
                dataStore.edit {
                    it[dataStoreKey] = true
                }
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


}