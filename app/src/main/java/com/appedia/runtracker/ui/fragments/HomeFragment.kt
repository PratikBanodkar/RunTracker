package com.appedia.runtracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentHomeBinding
import com.appedia.runtracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel : MainViewModel by viewModels()
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabNewRun.setOnClickListener {
            navigateToTrackingFragment()
        }
    }

    private fun navigateToTrackingFragment() {
        if(findNavController().currentDestination?.id == R.id.homeFragment)
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTrackingFragment())
    }
}