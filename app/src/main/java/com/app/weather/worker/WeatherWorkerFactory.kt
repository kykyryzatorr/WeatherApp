package com.app.weather.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.app.weather.api.WeatherApi
import com.app.weather.api.response.WeatherResponse
import com.app.weather.data.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow

class WeatherWorkerFactory(
    val weatherApi: WeatherApi,
    val dataStore: DataStoreManager): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        if(workerClassName == RequestWeatherWorker::class.java.name){
            return RequestWeatherWorker(appContext, workerParameters, weatherApi, dataStore)
        }
        return null
    }

}