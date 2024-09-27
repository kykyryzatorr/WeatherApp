package com.app.weather.injection

import android.content.Context
import androidx.work.WorkManager
import com.app.weather.BuildConfig
import com.app.weather.api.ApiModule
import com.app.weather.api.WeatherApi
import com.app.weather.data.DataStoreManager
import com.app.weather.worker.WeatherWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWeatherApi() : WeatherApi {
        return ApiModule().createWeatherApi(
            BuildConfig.API_BASE_URL,
            BuildConfig.API_KEY
        )
    }

    @Singleton
    @Provides
    fun provideWeatherWorkerFactory(@ApplicationContext context: Context): WeatherWorkerFactory {
        return WeatherWorkerFactory(provideWeatherApi(), provideDataStoreManager(context))
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context = context)
}