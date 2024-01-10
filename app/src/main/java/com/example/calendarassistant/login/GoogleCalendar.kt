package com.example.calendarassistant.login

import android.accounts.Account
import android.content.Context
import android.content.Intent
import com.example.calendarassistant.R
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.calendar.CalendarInformation
import com.example.calendarassistant.model.calendar.Calendars
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
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

    fun clearEvents() {
        _events.value = listOf()
    }

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

    fun getUpcomingEventsOneDayFromStartDate(email: String, startDate: LocalDate) {
        // Convert startDate (LocalDate) to java.util.Date at the start of the day
        val startDateUtilDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        // Convert startDate to com.google.api.client.util.DateTime
        val startDateTime = DateTime(startDateUtilDate)
        // Calculate the next day
        val nextDay = startDate.plusDays(1)
        val nextDayUtilDate = Date.from(nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant())
        // Convert nextDay to com.google.api.client.util.DateTime
        val nextDayDateTime = DateTime(nextDayUtilDate)
        // Now fetch events between startDateTime and nextDayDateTime
        getUpcomingEvents(email, startDateTime, nextDayDateTime)
    }

    fun getUpcomingEventsOneDayFromToday(email: String) {
        val now = com.google.api.client.util.DateTime(System.currentTimeMillis())
        val nowDate = Date(now.value) // Convert to java.util.Date
        val localDate: LocalDate = nowDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()                 // Convert to java.time.LocalDate
        val tomorrow = localDate.plusDays(1)                // Add a day
        // Convert back to com.google.api.client.util.DateTime
        val tomorrowDateTime: DateTime = DateTime(Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        getUpcomingEvents(email,now,tomorrowDateTime)
    }

    fun getUpcomingEventsOneWeekFromToday(email: String) {
        val now = com.google.api.client.util.DateTime(System.currentTimeMillis())
        val nowDate = Date(now.value) // Convert to java.util.Date
        val localDate: LocalDate = nowDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()                 // Convert to java.time.LocalDate
        val nextWeek = localDate.plusWeeks(1)                 // Add a week
        val nextWeekDateTime: DateTime = DateTime(Date.from(nextWeek.atStartOfDay(ZoneId.systemDefault()).toInstant()))         // Convert back to com.google.api.client.util.DateTime
        getUpcomingEvents(email,now,nextWeekDateTime)
    }


    /**
     * retreives events with email-adress, startdate for the events, end-date for the events
     */
        fun getUpcomingEvents(email: String, startDate:DateTime, endDate:DateTime) {
        val calendarService = getCalendarService(email)
        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                            .setMaxResults(100)
                            .setTimeMin(startDate)
                            .setTimeMax(endDate)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute()

                        val items = events.items

                        for (event in items) {
                            val eventSum = event.summary
                            val startDateTime = event?.start?.dateTime?.value?.div(1000)
                            val startDate = event?.start?.date?.value?.div(1000)
                            val location = event?.location
                            val endDateTime = event?.end?.dateTime?.value?.div(1000)
                            val endDate = event?.end?.date?.value?.div(1000)
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
