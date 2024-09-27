package com.app.weather.api.response

import com.squareup.moshi.Json

data class WeatherResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "name")
    val name: String,
    @Json(name = "weather")
    val weather: List<WeatherItem>,
    @Json(name = "main")
    val main: MainWeather,
    @Json(name = "wind")
    val wind: Wind,
)

data class WeatherItem(
    @Json(name = "main")
    val main: String,
    @Json(name = "description")
    val description: String,
)

data class MainWeather(
    @Json(name = "temp")
    val temp: Float,
    @Json(name = "temp_min")
    val temp_min: Float,
    @Json(name = "temp_max")
    val temp_max: Float,
)

data class Wind(
    @Json(name = "speed")
    val speed: Float,
    @Json(name = "deg")
    val deg: Integer,
)
