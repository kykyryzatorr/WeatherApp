package com.app.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.app.weather.data.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    val dataStoreManager: DataStoreManager
) : ViewModel() {}