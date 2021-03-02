package com.example.codingchallenge.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.FragmentDetailsPageBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailsPage : Fragment() {


    private var _binding: FragmentDetailsPageBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsPageArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayMovieInformation(view)
        initializeToolbar(view)
    }

    private fun initializeToolbar(view: View) {
        binding.detailsPageToolbar.setOnClickListener {
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
        time?.let{
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
        _binding = null
    }
}