package com.app.weather.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class ApiModule {

    companion object {
        val moshi by lazy {
            val moshiBuilder = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
            moshiBuilder.build()
        }

        //Moshi Extension functions
        inline fun <reified T> String.fromJson(): T? {
            val jsonAdapter = moshi.adapter(T::class.java)
            return jsonAdapter.fromJson(this)
        }

        inline fun <reified T> T.toJson(): String {
            val jsonAdapter = moshi.adapter(T::class.java)
            return jsonAdapter.toJson(this)
        }
    }

    // Retrofit Request Interceptor to add the API Key to every request
    class AuthenticationInterceptor(_key:String): Interceptor {

        private val key:String = _key

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalUrl = chain.request().url

            val newUrl = originalUrl.newBuilder()
                .addQueryParameter(WeatherApi.PARAM_APPID, key)
                .build()

            val newRequest =
                chain.request().newBuilder().url(newUrl).build()

            return chain.proceed(newRequest)
        }
    }

    private fun retrofitBuilder(baseUrl : String) : Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }

    private fun getApiHttpClient(apiKey : String) : OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder
            .addInterceptor(AuthenticationInterceptor(apiKey))
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun getRetrofit(endpointURL: String,
                                         apiKey: String): Retrofit {
        return retrofitBuilder(endpointURL)
            .client(getApiHttpClient(apiKey))
            .build()
    }

    fun createWeatherApi(endpointURL: String, key: String): WeatherApi {
        val retrofit = getRetrofit(endpointURL, key)
        return retrofit.create(WeatherApi::class.java)
    }

}