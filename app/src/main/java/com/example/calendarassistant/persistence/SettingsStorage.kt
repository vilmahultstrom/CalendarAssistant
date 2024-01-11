package com.example.calendarassistant.persistence

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.calendarassistant.model.Settings
import com.example.calendarassistant.utilities.DateHelpers.localDateTimeToString
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

private const val TAG = "SettingsStorage"

object SettingsStorage {

    private const val PREFS_NAME = "SettingsPrefs"
    private const val KEY_SETTINGS = "saved_settings"
    private const val KEY_LASTUPDATE = "last_update"

    fun saveSettings(context: Context, settings: Settings, dateTime: LocalDateTime) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(settings)

        prefs.edit {
            putString(KEY_SETTINGS, json)
            putString(KEY_LASTUPDATE, localDateTimeToString(dateTime))
        }
    }

    fun getSavedSettings(context: Context): Pair<Settings, String?>? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        return try {
            //clearSavedSettings(context)
            val json = prefs.getString(KEY_SETTINGS, null)
            if (json != null) {
                val type = object : TypeToken<Settings>() {}.type
                val realSettings = Gson().fromJson<Settings>(json, type)

                Pair(
                    realSettings,
                    prefs.getString(KEY_LASTUPDATE, null)
                )
            } else {
                Log.e(TAG, "No stored settings found.")
                null
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Error parsing stored JSON", e)
            null
        }
    }

    // used to clear the storage during development
    private fun clearSavedSettings(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit {
            remove(KEY_SETTINGS)
            remove(KEY_LASTUPDATE)
        }
    }
}

