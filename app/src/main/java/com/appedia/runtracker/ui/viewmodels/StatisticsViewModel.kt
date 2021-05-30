package com.appedia.runtracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.appedia.runtracker.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    var totalRunTime: LiveData<Long> = mainRepository.getTotalRunDurationInMillis()
    var totalDistanceRun: LiveData<Int> = mainRepository.getTotalDistanceMTR()
    var totalCaloriesBurned: LiveData<Int> = mainRepository.getTotalCaloriesBurned()
    var averageSpeed: LiveData<Float> = mainRepository.getTotalAvgSpeedKMPH()

}