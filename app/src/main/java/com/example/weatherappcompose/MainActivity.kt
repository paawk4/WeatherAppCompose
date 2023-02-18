package com.example.weatherappcompose

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcompose.data.WeatherModel
import com.example.weatherappcompose.screens.DialogSearch
import com.example.weatherappcompose.screens.MainCard
import com.example.weatherappcompose.screens.TabLayout
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val daysList = remember {
                mutableStateOf(listOf<WeatherModel>())
            }
            val dialogState = remember {
                mutableStateOf(false)
            }
            val currentDay = remember {
                mutableStateOf(
                    WeatherModel(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                )
            }
            if (dialogState.value){
                DialogSearch(dialogState, onSubmit = {
                    getData(it, this, daysList, currentDay)
                })
            }

            getData("London", this, daysList, currentDay)
            Column {
                MainCard(currentDay, onClickSync = {
                    getData("London", this@MainActivity, daysList, currentDay)
                }, onClickSearch = {
                    dialogState.value = true
                })
                TabLayout(daysList, currentDay)
            }
        }
    }
}

private fun getData(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {

    val url =
        "https://api.weatherapi.com/v1/forecast.json?key=" +
                "6368b3d05c944f9e9d8113827230902" +
                "&q=$city" +
                "&days=3" +
                "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Method.GET,
        url,
        {
            val list = getWeatherByDays(it)
            currentDay.value = list[0]
            daysList.value = list
        },
        { Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show() }
    )
    queue.add(stringRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}
