package com.example.mapit.weather

import android.location.Location

data class WeatherData(
    val temp :  String ,
    val wind : String ,
    val humidity : String ,
    val condition :  String
)

data class WeatherState(
    val isLoading : Boolean  =  false,
    val temp : String,
   // val weatherData : WeatherData? = null,
    val current : String = ""
)