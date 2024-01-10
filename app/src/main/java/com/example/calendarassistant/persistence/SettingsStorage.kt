/*
package com.example.calendarassistant.persistence

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

object SettingsStorage {

    private const val PREFS_NAME = "WeatherForecastPrefs"
    private const val KEY_FORECAST = "saved_forecast"
    private const val KEY_LASTUPDATE = "last_update"

    fun saveSettings(context: Context, forecast: WeatherForecast, dateTime: LocalDateTime) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(forecast)

        prefs.edit {
            putString(KEY_FORECAST, json)
            putString(KEY_LASTUPDATE, localDateTimeToString(dateTime))
        }
    }

    fun getSavedSettings(context: Context): Pair<WeatherForecast, String?>? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        return try {
            //clearSavedForecast(context)
            val json = prefs.getString(KEY_FORECAST, null)
            if (json != null) {
                val type = object : TypeToken<WeatherForecast>() {}.type
                val realForecast = Gson().fromJson<WeatherForecast>(json, type)

                Pair(
                    realForecast,
                    prefs.getString(KEY_LASTUPDATE, null)
                )
            } else {
                Log.e("ForecastStorage", "No stored forecast found.")
                null
            }
        } catch (e: JsonSyntaxException) {
            Log.e("ForecastStorage", "Error parsing stored JSON", e)
            null
        }
    }

    // used to clear the storage during development
    private fun clearSavedForecast(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit {
            remove(KEY_FORECAST)
            remove(KEY_LASTUPDATE)
        }
    }
}

*/
