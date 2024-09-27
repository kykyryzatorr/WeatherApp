package com.app.weather

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.app.weather.worker.RequestWeatherWorker
import com.app.weather.worker.WeatherWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class WeatherApplication : Application(), Configuration.Provider {

    companion object {
        const val LOGTAG = "WeatherApp"
        private var instance : WeatherApplication? = null

        fun startWeatherWorker() {
            val jobConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            var workRequest =
                PeriodicWorkRequestBuilder<RequestWeatherWorker>(2, TimeUnit.HOURS)
                    .setConstraints(jobConstraints)
                    .addTag(RequestWeatherWorker::class.java.simpleName)
                    .build()

            WorkManager.getInstance(instance!!.applicationContext).enqueueUniquePeriodicWork(
                RequestWeatherWorker::class.java.simpleName,
                ExistingPeriodicWorkPolicy.REPLACE, workRequest
            )

        }
    }

    init {
        instance = this
    }

    @Inject
    lateinit var workerFactory: WeatherWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        startWeatherWorker()
    }
}