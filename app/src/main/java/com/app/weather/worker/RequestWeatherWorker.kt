package com.app.weather.worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.weather.WeatherApplication
import com.app.weather.api.ApiModule
import com.app.weather.api.ApiModule.Companion.toJson
import com.app.weather.api.WeatherApi
import com.app.weather.api.response.WeatherResponse
import com.app.weather.common.hasLocationPermissions
import com.app.weather.data.DataStoreManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject


@HiltWorker
class RequestWeatherWorker @AssistedInject constructor(@Assisted val appContext: Context,
                                                       @Assisted workerParams: WorkerParameters,
                                                       val weatherApi: WeatherApi,
                                                       val dataStore: DataStoreManager):
    CoroutineWorker(appContext, workerParams) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    @SuppressLint("MissingPermission")
    override suspend fun doWork() : Result {
        Log.d(WeatherApplication.LOGTAG, "*= doWork")
        withContext(Dispatchers.IO) {
            val lastLoc =
                    if (appContext.hasLocationPermissions()) {
                        fusedLocationClient.lastLocation
                    } else {
                        null
                    }

            lastLoc?.addOnSuccessListener {
                runBlocking {
                    val response = weatherApi.getCurrentWeather(it.latitude,it.longitude)
                    if(response.isSuccessful){
                        val newWeather = response.body()
                        dataStore.saveWeather(newWeather)
                    }
                }
            }
        }
        return Result.success()
    }
}