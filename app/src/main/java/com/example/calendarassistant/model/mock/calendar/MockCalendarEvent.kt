package com.example.calendarassistant.model.mock.calendar
import java.time.LocalDateTime

data class MockCalendarEvent(
    val start: LocalDateTime,
    val summary: String,
    val location: String
)

object MockEvent {

    private val events: List<MockCalendarEvent> = listOf(
        MockCalendarEvent(
            start = LocalDateTime.now().plusMinutes(90),
            summary = "Föreläsning - Mjukvarukonstruktion, projektkurs (HI1036)",
            location = "T67 Hälsovägen"
        ),
        MockCalendarEvent(
            start = LocalDateTime.now().plusHours(4),
            summary = "Föreläsning - Nätverkssäkerhet, grundkurs (HI1023)",
            location = "T61 Hälsovägen"
        ),
    )

    fun getMockEvents() = events



}
