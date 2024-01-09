package com.example.calendarassistant.login

import android.accounts.Account
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "CalendarGoogle"


class GoogleCalendar @Inject constructor(private val context: Context) {
    companion object {
        private const val APPLICATION_NAME = "Google Calendar API Kotlin Android Quickstart"
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
    }
    
    private fun getCalendarService(email: String): Calendar {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, SCOPES
        ).setSelectedAccount(Account(email, "com.example.calendarassistant"))

        return Calendar.Builder(
            AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential
        ).setApplicationName(APPLICATION_NAME)
            .build()
    }

    fun getUpcomingEvents(email: String) {
        val calendarService = getCalendarService(email)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = com.google.api.client.util.DateTime(System.currentTimeMillis())
                val events = calendarService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                val items = events.items

                withContext(Dispatchers.Main) {
                    if (items.isEmpty()) {
                        Log.d(TAG, "no events present")
                        // Update UI to show no events
                    } else {
                        Log.d(TAG, "found events")
                        Log.d(TAG, items.toString())
                        Log.d(TAG, "good")
                        var itemList = ""
                        for (i in items) {
                            itemList += i.toString() + "\n";
                        }
                        Log.d(TAG, itemList)
                        // Update UI to show events
                        for (event in items) {
                            val start = event.start.dateTime ?: event.start.date
                            // Update UI with event details
                        }
                    }
                }
            } catch (e: UserRecoverableAuthIOException) { //user needs to accept that the app will have access to users calendar. This exception-handling is absolutely necessary for retreiving events in the app.
                val intent = e.intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this line
                context.startActivity(intent)
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
