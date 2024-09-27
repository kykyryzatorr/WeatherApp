package com.app.weather.api

import retrofit2.Response
import com.app.weather.api.response.*
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    companion object {
        const val PARAM_APPID = "appId"
        const val PARAM_LAT = "lat"
        const val PARAM_LON = "lon"
        const val PARAM_UNITS = "units"

        const val PARAM_DEFAULT_METRIC = "metric"
    }

    @GET("weather")
    suspend fun getCurrentWeather(@Query(PARAM_LAT) lat : Double,
                                  @Query (PARAM_LON) lon : Double,
                                    @Query(PARAM_UNITS) units : String = PARAM_DEFAULT_METRIC)
        : Response<WeatherResponse>
}