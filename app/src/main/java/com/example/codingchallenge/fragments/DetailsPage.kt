package com.example.codingchallenge.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.codingchallenge.Constants.Companion.DATA_STORE
import com.example.codingchallenge.Constants.Companion.LATEST_FIELD_KEY
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.FragmentDetailsPageBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DetailsPage : Fragment() {


    private var _binding: FragmentDetailsPageBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsPageArgs by navArgs()
    private lateinit var dataStore : DataStore<Preferences>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStore = requireContext().createDataStore(DATA_STORE)
        displayMovieInformation(view)
        initializeToolbar(view)
        handleBackPress()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            removeDataStorePreference()
            Navigation.findNavController(requireView()).navigateUp()
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            onBackPressedCallback
        )
    }

    private fun removeDataStorePreference() {
        val latestMovieClicked = intPreferencesKey(LATEST_FIELD_KEY)
        lifecycleScope.launch(IO) {
            dataStore.edit {
                it[latestMovieClicked] = -1
            }
        }
    }

    private fun initializeToolbar(view: View) {
        binding.detailsPageToolbar.setOnClickListener {
            removeDataStorePreference()
            Navigation.findNavController(view).navigateUp()
        }

    }

    private fun displayMovieInformation(view: View) {
        val details = args.movieDetails
        binding.detailsPageBuy.text =
            resources.getString(R.string.detailsPage_buy, details?.buyPrice.toString())
        binding.detailsPageRent.text = resources.getString(
            R.string.detailsPage_rent,
            details?.rentPrice.toString()
        )
        binding.detailsPageDescription.text = details?.description
        binding.detailsPageDuration.text =
            getString(R.string.detailsPage_duration, convertTime(details?.duration))
        binding.detailsPageGenre.text = getString(R.string.detailsPage_genre, details?.genre)
        binding.detailsPageTitle.text = details?.trackName
        binding.detailsPageReleased.text = getString(
            R.string.detailsPage_released, details?.releaseDate?.substring(
                0,
                10
            )
        )
        Glide.with(view)
            .load(details?.artWork)
            .placeholder(R.drawable.ic_place_holder_100)
            .into(binding.detailsPageImage)
    }

    private fun convertTime(time: Int?): String {
        time?.let {
            var timeMilli = it
            val hours = (timeMilli / 3_600_000)
            timeMilli -= hours * 3_600_000
            val minutes = (timeMilli / 60_000)
            timeMilli -= minutes * 60_000

            val hourString = if (hours <= 9) "0$hours" else hours
            val minuteString = if (minutes <= 9) "0$minutes" else minutes
            return "$hourString hr : $minuteString min"
        }
        return "N/A"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
        _binding = null
    }
}