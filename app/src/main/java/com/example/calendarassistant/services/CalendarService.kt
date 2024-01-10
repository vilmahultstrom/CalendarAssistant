package com.example.calendarassistant.services

import com.example.calendarassistant.login.GoogleAuthClient
import com.example.calendarassistant.login.GoogleCalendar

class CalendarService(
    private val googleCalendar: GoogleCalendar,
    private val googleAuthClient: GoogleAuthClient
) {
    var events = googleCalendar.events
    //var signedInUser = googleAuthClient.getSignedInUser()

    fun getUpcomingEvents() {
        val email = googleAuthClient.getSignedInUser()!!.email
        if (email != null) {
            googleCalendar.getUpcomingEventsOneWeekFromToday(email)
        }
    }
}