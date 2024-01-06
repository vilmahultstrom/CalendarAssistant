package com.example.calendarassistant.model.mock.calendar
import android.util.Log
import com.example.calendarassistant.utilities.DateHelpers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "MockEvent"
object MockEvent {

    private val events: List<MockCalendarEvent> = listOf(
        MockCalendarEvent(
            start = ZonedDateTime.now(ZoneId.of("Z")).plusMinutes(300).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            summary = "Föreläsning - Mjukvarukonstruktion, projektkurs (HI1036)",
            location = "T67 Hälsovägen"
        ),
        MockCalendarEvent(
            start = ZonedDateTime.now(ZoneId.of("Z")).plusHours(6).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), // Så kommer tiden från google
            summary = "Föreläsning - Nätverkssäkerhet, grundkurs (HI1023)",
            location = "T61 Hälsovägen"
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
            val formatted = startTime?.format(customFormatter) ?: throw NumberFormatException("Start time was null")

            val newEvent = MockCalendarEvent(formatted, element.summary, element.location)
            newEvents.add(newEvent)
        }
        return newEvents
    }


}
data class MockCalendarEvent(
    var start: String,
    val summary: String,
    val location: String
)




