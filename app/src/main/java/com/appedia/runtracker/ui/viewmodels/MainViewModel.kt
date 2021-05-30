package com.appedia.runtracker.ui.viewmodels

import androidx.lifecycle.*
import com.appedia.runtracker.data.db.entities.Run
import com.appedia.runtracker.data.repositories.MainRepository
import com.appedia.runtracker.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private var _sortFilterLiveData = MutableLiveData(SortType.DATE)

    val runs: LiveData<List<Run>> = Transformations.switchMap(_sortFilterLiveData) {
        when (it) {
            SortType.DATE -> mainRepository.getAllRunsSortedByDate()
            SortType.RUNNING_TIME -> mainRepository.getAllRunsSortedByRunDuration()
            SortType.DISTANCE -> mainRepository.getAllRunsSortedByDistanceMTR()
            SortType.AVERAGE_SPEED -> mainRepository.getAllRunsSortedByAvgSpeedKMPH()
            SortType.CALORIES_BURNED -> mainRepository.getAllRunsSortedByCaloriesBurned()
            else -> mainRepository.getAllRunsSortedByDate()
        }
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

    fun updateFilterOption(position: Int) {
        when (position) {
            0 -> _sortFilterLiveData.postValue(SortType.DATE)
            1 -> _sortFilterLiveData.postValue(SortType.RUNNING_TIME)
            2 -> _sortFilterLiveData.postValue(SortType.DISTANCE)
            3 -> _sortFilterLiveData.postValue(SortType.AVERAGE_SPEED)
            4 -> _sortFilterLiveData.postValue(SortType.CALORIES_BURNED)
        }
    }


}