package com.example.calendarassistant.services

import com.example.calendarassistant.login.GoogleCalendar

class CalendarService (private val googleCalendar: GoogleCalendar){
    var events = googleCalendar.events

    fun getUpcomingEvents(email:String){googleCalendar.getUpcomingEvents(email)}
}