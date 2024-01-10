package com.example.calendarassistant.services

import android.util.Log
import com.example.calendarassistant.login.GoogleAuthClient
import com.example.calendarassistant.login.GoogleCalendar
import java.time.LocalDate

class CalendarService(
    private val googleCalendar: GoogleCalendar,
    private val googleAuthClient: GoogleAuthClient
) {
    var events = googleCalendar.events
    //var signedInUser = googleAuthClient.getSignedInUser()

    /**
     * retreives all events for one week starting today
     */
    fun getUpcomingEventsForOneWeek() {
        val user = googleAuthClient.getSignedInUser()
        if (user?.email != null) {
            googleCalendar.getUpcomingEventsOneWeekFromToday(user.email)
        }
    }


    /**
     * retreives all events for today
     */
    fun getUpcomingEventsForOneDay() {
        val signedInUser = googleAuthClient.getSignedInUser()
        if (signedInUser != null) {
            val email = signedInUser.email
            if (email != null) {
                googleCalendar.getUpcomingEventsOneDayFromToday(email)
            } else {
                Log.e("CalendarService", "Email is null")
            }
        } else {
            Log.e("CalendarService", "Signed-in user is null")
        }
    }


    /**
    * retreives all events for input date (one day)
    */
    fun getUpcomingEventsForOneDay(startDate: LocalDate) {
        val user = googleAuthClient.getSignedInUser()
        if (user?.email != null) {
            googleCalendar.getUpcomingEventsOneDayFromStartDate(user.email, startDate)
        }
    }


    fun clearEvents() {
        Log.e("CalendarService", "Signed-in user is null")
        googleCalendar.clearEvents()
    }
}