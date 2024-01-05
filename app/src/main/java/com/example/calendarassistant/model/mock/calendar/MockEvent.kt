package com.example.calendarassistant.model.mock.calendar

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "MockEvent"

object MockEvent {

    private val events: List<MockCalendarEvent> = listOf(
        MockCalendarEvent(
            start = ZonedDateTime.now(ZoneId.of("Z")).plusMinutes(300)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            summary = "Föreläsning - Mjukvarukonstruktion, projektkurs (HI1036)",
            location = "T67 Hälsovägen"
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
    private var nextEventInformation = MutableSharedFlow<NextEventInformation>()
    suspend fun setNextEventInformation(
        departureTimeHHMM: String,
        departureTime: String?,
        endLocation: Pair<Double?, Double?>
    ) {
        nextEventInformation.emit(
            NextEventInformation(
                departureTime = departureTime, departureTimeHHMM = departureTimeHHMM,
                destinationCoordinates = endLocation
            )
        )
    }

    fun getNextEventInformation(): SharedFlow<NextEventInformation> =
        MockEvent.nextEventInformation.asSharedFlow()

    fun getEvents(): List<MockCalendarEvent> {
        return events
    }

    fun getMockEvents() = events
}

// TODO: Fler parametrar här?
//  Typ slut-tid,
//  vilken importerad kalender det tillhör,
//   egna notes om eventet
data class MockCalendarEvent(
    val start: String,
    val summary: String,
    val location: String
)

data class NextEventInformation(
    var departureTimeHHMM: String? = "",
    var departureTime: String? = "",
    var destinationCoordinates: Pair<Double?, Double?> = Pair(null, null)
)


