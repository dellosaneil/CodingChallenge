package com.example.codingchallenge.fragments.detailsPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.codingchallenge.databinding.FragmentDetailsPageBinding

class DetailsPage : Fragment() {


    private var _binding: FragmentDetailsPageBinding? = null
    private val binding get() = _binding!!
    private val args : DetailsPageArgs by navArgs()
    private val TAG = "DetailsPage"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsPageBinding.inflate(inflater, container, false)
        args.movieDetails?.let{
            Log.i(TAG, "onCreateView: $it ")
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}