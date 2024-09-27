package com.app.weather.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.weather.api.ApiModule.Companion.fromJson
import com.app.weather.api.ApiModule.Companion.toJson
import com.app.weather.api.response.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(context: Context) {
    companion object {
        const val PREFS: String = "WeatherAppPrefs"
        const val PREF_KEY_WEATHER: String = "WeatherResponse"
        private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS)
    }

    private val weatherKey = stringPreferencesKey(PREF_KEY_WEATHER)
    private val prefDataStore = context.preferencesDataStore

    suspend fun saveWeather(weather: WeatherResponse?){
        prefDataStore.edit { dataStore ->
            dataStore[weatherKey] = weather.toJson()
        }
    }

    val weatherFlow: Flow<WeatherResponse?> = prefDataStore.data
        .map { preferences ->
            preferences[weatherKey]?.fromJson()
        }
}