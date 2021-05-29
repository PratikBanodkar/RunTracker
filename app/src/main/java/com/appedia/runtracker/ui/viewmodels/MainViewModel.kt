package com.appedia.runtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appedia.runtracker.data.db.entities.Run
import com.appedia.runtracker.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val runsSortedByDate = mainRepository.getAllRunsSortedByDate()

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }


}