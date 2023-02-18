package com.example.weatherappcompose.data

data class WeatherModel(
    val city: String,
    val time: String,
    val currentTemp: String,
    val conditionText: String,
    val conditionIcon: String,
    val minTemp: String,
    val maxTemp: String,
    val hoursWeather: String
)
