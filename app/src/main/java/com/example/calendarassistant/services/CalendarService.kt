package com.example.calendarassistant.services

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
        val email = googleAuthClient.getSignedInUser()!!.email
        if (email != null) {
            googleCalendar.getUpcomingEventsOneWeekFromToday(email)
        }
    }

    /**
     * retreives all events for today
     */
    fun getUpcomingEventsForOneDay() {
        val email = googleAuthClient.getSignedInUser()!!.email
        if (email != null) {
            googleCalendar.getUpcomingEventsOneDayFromStartDate(email, LocalDate.now())
        }
    }

    /**
    * retreives all events for input date (one day)
    */
    fun getUpcomingEventsForOneDay(startDate: LocalDate){
        val email = googleAuthClient.getSignedInUser()!!.email
        if (email != null) {
            googleCalendar.getUpcomingEventsOneDayFromStartDate(email, startDate)
        }
    }
}