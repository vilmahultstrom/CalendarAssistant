package com.example.calendarassistant.model.mock.calendar

import com.example.calendarassistant.utilities.DateHelpers
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "MockEvent"

object MockEvent {

    private val events: List<MockCalendarEvent> = listOf(
        MockCalendarEvent(
            start = ZonedDateTime.now(ZoneId.of("Z")).plusMinutes(45)//300)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            summary = "Föreläsning - Mjukvarukonstruktion, projektkurs (HI1036)",
            location = "linde" //"T67 Hälsovägen"
        ),
        MockCalendarEvent(
            start = ZonedDateTime.now(ZoneId.of("Z")).plusHours(6)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), // Så kommer tiden från google
            summary = "Föreläsning - Nätverkssäkerhet, grundkurs (HI1023)",
            location = "T61 Hälsovägen"
        ),
        MockCalendarEvent(
            start = ZonedDateTime.now(ZoneId.of("Z")).plusHours(8)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            summary = "Praktik - Krukmejeri, avancerad kurs (HI1234)",
            location = "T24 Hälsovägen"
        ),
    )


    fun getMockEvents(): List<MockCalendarEvent> {
        return events
    }

    fun getMockEventsFormattedConvertedTime(): MutableList<MockCalendarEvent> {
        val newEvents = mutableListOf<MockCalendarEvent>()
        for (element in events) {
            val startTime = DateHelpers.convertToSystemTimeZone(element.start)
            val customFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val startTimeFormatted = startTime?.format(customFormatter) ?: throw NumberFormatException("Start time was null")

            val newEvent = MockCalendarEvent(startTimeFormatted, element.summary, element.location)
            newEvents.add(newEvent)
        }
        return newEvents
    }


}

// TODO: Fler parametrar här?
//  Typ slut-tid,
//  vilken importerad kalender det tillhör,
//   egna notes om eventet
data class MockCalendarEvent(
    var start: String,
    val summary: String,
    val location: String
)



