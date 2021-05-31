package com.appedia.runtracker.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentStatisticsBinding
import com.appedia.runtracker.ui.viewmodels.StatisticsViewModel
import com.appedia.runtracker.util.ChartOptionType
import com.appedia.runtracker.util.ChartUtils.generateLineDataSet
import com.appedia.runtracker.util.Constants.getFormattedCaloriesForUI
import com.appedia.runtracker.util.Constants.getFormattedDistanceForUI
import com.appedia.runtracker.util.Constants.getFormattedRunTimeForUI
import com.appedia.runtracker.util.Constants.getFormattedSpeedForUI
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var listOfAllAvgSpeeds: List<Entry>
    private lateinit var listOfAllRunTimes: List<Entry>
    private lateinit var listOfAllRunDistances: List<Entry>
    private lateinit var listOfAllCaloriesBurned: List<Entry>

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
        initRadioGroupListener()
        setupLineChart()
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

        viewModel.allAvgSpeedsList.observe(viewLifecycleOwner, {
            it?.let {
                listOfAllAvgSpeeds = it.indices.map { i ->
                    Entry(i.toFloat(), it[i])
                }
            }
        })

        viewModel.allRunTimesList.observe(viewLifecycleOwner, {
            it?.let {
                listOfAllRunTimes = it.indices.map { i ->
                    Entry(i.toFloat(), it[i].toFloat())
                }
            }
        })

        viewModel.allRunningDistanceList.observe(viewLifecycleOwner, {
            it?.let {
                listOfAllRunDistances = it.indices.map { i ->
                    Entry(i.toFloat(), it[i].toFloat())
                }
            }
        })

        viewModel.allCaloriesBurnedList.observe(viewLifecycleOwner, {
            it?.let {
                listOfAllCaloriesBurned = it.indices.map { i ->
                    Entry(i.toFloat(), it[i].toFloat())
                }
            }
        })
    }

    private fun initRadioGroupListener() {
        binding.radioGroupChartOptions.setOnCheckedChangeListener { _, _ ->
            when (binding.radioGroupChartOptions.checkedRadioButtonId) {
                R.id.radioButtonAvgSpeed -> {
                    updateLineChart(ChartOptionType.AVERAGE_SPEED)
                }
                R.id.radioButtonCaloriesBurned -> {
                    updateLineChart(ChartOptionType.CALORIES_BURNED)
                }
                R.id.radioButtonRunningTime -> {
                    updateLineChart(ChartOptionType.RUNNING_TIME)
                }
                R.id.radioButtonRunningDistance -> {
                    updateLineChart(ChartOptionType.DISTANCE)
                }
            }
        }
    }

    private fun setupLineChart() {
        binding.lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.lineChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.lineChart.axisRight.apply {
            axisLineColor = Color.WHITE
            setDrawLabels(false)
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
    }

    private fun generateDataValuesForLineDataSet(chartOptionType: ChartOptionType): List<Entry> {
        return when (chartOptionType) {
            ChartOptionType.AVERAGE_SPEED -> {
                listOfAllAvgSpeeds
            }
            ChartOptionType.RUNNING_TIME -> {
                listOfAllRunTimes
            }
            ChartOptionType.DISTANCE -> {
                listOfAllRunDistances
            }
            ChartOptionType.CALORIES_BURNED -> {
                listOfAllCaloriesBurned
            }
        }
    }

    private fun updateLineChart(chartOptionType: ChartOptionType) {
        val dataValues = generateDataValuesForLineDataSet(chartOptionType)
        val lineDataSet = generateLineDataSet(chartOptionType, dataValues, requireContext())
        val allDataSets = mutableListOf<ILineDataSet>().apply {
            add(lineDataSet)
        }
        val lineData = LineData(allDataSets)
        binding.lineChart.apply {
            data = lineData
            legend.isEnabled = false
            description.isEnabled = false
            notifyDataSetChanged()
            invalidate()
        }
    }

}