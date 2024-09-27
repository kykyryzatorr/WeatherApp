package com.app.weather.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.weather.R
import com.app.weather.WeatherApplication
import com.app.weather.api.response.WeatherResponse
import com.app.weather.databinding.FragmentMainBinding
import com.app.weather.presentation.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.app.weather.common.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    protected val weatherViewModel : WeatherViewModel by activityViewModels ()

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeWeather()

        with(binding) {
            btnRetry.setOnClickListener {
                checkForPrerequisites()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkForPrerequisites()
    }

    private fun showStatus() {
        with(binding) {
            llStatus.isVisible = true
            clWeather.isVisible = false
        }
    }

    private fun showWeather() {
        with(binding) {
            llStatus.isVisible = false
            clWeather.isVisible = true
        }
    }

    private fun observeWeather() {
        with(requireContext()) {
            if (hasLocationPermissions() && hasInternet()) {
                with(weatherViewModel) {
                    viewModelScope.launch {
                        dataStoreManager.weatherFlow.collect { weather ->
                            populateWeatherUI(weather)
                        }
                    }
                }
            }
        }
    }

    private fun checkForPrerequisites() {
        with(binding) {
            if (!requireContext().hasLocationPermissions()) {
                showStatus()
                tvStatus.text =
                    "Location Permissions are required - please enable for this app in Android Settings"
                tvStatus.setTextColor(Color.RED)
                btnRetry.isVisible = true
            } else if (!requireContext().hasInternet()) {
                showStatus()
                binding.tvStatus.text = "Internet connection required - please enable"
                binding.tvStatus.setTextColor(Color.RED)
                btnRetry.isVisible = true
            } else {
                showStatus()
                WeatherApplication.startWeatherWorker()
                observeWeather()
                binding.tvStatus.setText(R.string.status_not_available)
                binding.tvStatus.setTextColor(Color.BLACK)
                btnRetry.isVisible = false
            }
        }
    }

    private fun populateWeatherUI(weather : WeatherResponse?) {
        with(binding){
            weather?.let{
                tvLocation.text = weather.name
                val weatherBuilder = StringBuilder()
                it.weather?.forEach{ item ->
                    with(weatherBuilder){
                        if(length > 0) append("\n")
                        append(item.description)
                    }
                }
                tvWeather.text = weatherBuilder.toString()
                tvTemperature.text = "${weather.main.temp} C"
                tvTemperatureRange.text = "(Min:${weather.main.temp_min} C - Max:${weather.main.temp_max} C)"
                tvWind.text = "${weather.wind.speed}m/s (Direction: ${weather.wind.deg}deg)"

                showWeather()
            }
        }
    }
}