package com.appedia.runtracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.appedia.runtracker.databinding.FragmentStatisticsBinding
import com.appedia.runtracker.ui.viewmodels.StatisticsViewModel
import com.appedia.runtracker.util.Constants.getFormattedCaloriesForUI
import com.appedia.runtracker.util.Constants.getFormattedDistanceForUI
import com.appedia.runtracker.util.Constants.getFormattedRunTimeForUI
import com.appedia.runtracker.util.Constants.getFormattedSpeedForUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModelData()
    }

    private fun observeViewModelData() {
        viewModel.totalRunTime.observe(viewLifecycleOwner, {
            it?.let {
                binding.textViewTotalRunTimeValue.text = getFormattedRunTimeForUI(it)
            }
        })

        viewModel.totalDistanceRun.observe(viewLifecycleOwner, {
            it?.let {
                binding.textViewTotalDistanceRunValue.text = getFormattedDistanceForUI(it)
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, {
            it?.let {
                binding.textViewTotalCaloriesBurnedValue.text = getFormattedCaloriesForUI(it)
            }
        })

        viewModel.averageSpeed.observe(viewLifecycleOwner, {
            it?.let {
                binding.textViewAverageSpeedValue.text = getFormattedSpeedForUI(it)
            }
        })
    }
}