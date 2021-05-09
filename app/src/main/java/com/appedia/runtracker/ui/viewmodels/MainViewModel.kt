package com.appedia.runtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.appedia.runtracker.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mainRepository: MainRepository
) : ViewModel() {



}