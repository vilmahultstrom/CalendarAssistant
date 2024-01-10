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
import com.example.calendarassistant.R
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.calendar.CalendarInformation
import com.example.calendarassistant.model.calendar.Calendars
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "CalendarGoogle"


class GoogleCalendar @Inject constructor(private val context: Context) {
    companion object {
        private const val APPLICATION_NAME = "Google Calendar API Kotlin Android Quickstart"
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)

        @Volatile
        private var calendar: Calendar? = null
        private var currentEmail: String? = null
    }

    private var _events =
        MutableStateFlow<List<com.google.api.services.calendar.model.Event>>(listOf())
    var events: StateFlow<List<com.google.api.services.calendar.model.Event>> = _events

    private fun getCalendarService(email: String): Calendar {
        synchronized(this) {
            if (calendar != null && currentEmail == email) {
                return calendar!!
            }
            val credential = GoogleAccountCredential.usingOAuth2(
                context, SCOPES
            ).setSelectedAccount(Account(email, context.getString(R.string.package_name)))

            calendar = Calendar.Builder(
                AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential
            ).setApplicationName(APPLICATION_NAME)
                .build()

            currentEmail = email
            return calendar!!
        }
    }

    fun getUpcomingEvents(email: String) {
        val calendarService = getCalendarService(email)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = com.google.api.client.util.DateTime(System.currentTimeMillis())
                val calendars = calendarService.CalendarList().list().execute()
                val listItems = calendars.items

                val calendarList = mutableListOf<com.example.calendarassistant.model.calendar.Calendar>()



                for (calendar in listItems) {
                    val id = calendar.id
                    if (id != null) {
                        val summary = calendar.summary
                        val timeZone = calendar.timeZone
                        val backgroundColor = calendar.backgroundColor
                        val description = calendar.description
                        val calendarEvents = mutableListOf<CalendarEvent>()
                        val events = calendarService.events().list(id)
                            .setMaxResults(10)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute()

                        val items = events.items

                        for (event in items) {
                            val eventSum = event.summary
                            val startDateTime = event?.start?.dateTime?.value
                            val startDate = event?.start?.date?.value
                            val location = event?.location
                            val endDateTime = event?.end?.dateTime?.value
                            val endDate = event?.end?.date?.value
                            val calendarEvent: CalendarEvent = CalendarEvent(
                                summary = eventSum,
                                startDate = startDate,
                                startDateTime = startDateTime,
                                endDate = endDate,
                                endDateTime = endDateTime,
                                location = location
                            )
                            calendarEvents.add(calendarEvent)
                        }
                        val calendarInformation = CalendarInformation(
                            summary = summary,
                            id = id,
                            timeZone = timeZone,
                            backgroundColor = backgroundColor,
                            description = description
                        )

                        val newCalendar = com.example.calendarassistant.model.calendar.Calendar(
                            calendarInformation = calendarInformation,
                            calendarEvents = calendarEvents
                        )
                        calendarList.add(newCalendar)


                    }


                }
                Calendars.setCalendarList(calendarList)

            } catch (e: UserRecoverableAuthIOException) { //user needs to accept that the app will have access to users calendar. This exception-handling is absolutely necessary for retreiving events in the app.
                val intent = e.intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //:TODO Bättre lösning?
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
