package com.example.calendarassistant.utilities

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.absoluteValue

private const val TAG = "DateHelpers"

data class TimeToLeaveDisplay (
    val hhmmDisplay: String? = null,
    val onTime: Boolean? = null
)


object DateHelpers {

    fun getCurrentMonthDates(date: LocalDate): List<String> {
        val yearMonth = YearMonth.of(date.year, date.month)
        val daysInMonth = yearMonth.lengthOfMonth()

        return (1..daysInMonth).map { it.toString() }
    }



    fun convertUnixTimeToLocalDateTime(unixTime: Long): LocalDateTime {
        val instant = Instant.ofEpochSecond(unixTime)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }


    /**
     *  DateString format is "2024-01-19T09:00:00.000Z"
     *  Z indicates UTC, converts to local time
     */
    fun convertToSystemTimeZone(dateString: String): ZonedDateTime? {
        val utcDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        val systemTimeZone = ZoneId.systemDefault()
        return utcDateTime.withZoneSameInstant(systemTimeZone)
    }


    /**
     *  Returns a string with the format HHh:MMm from
     *  a seconds value.
     */
    fun getTimeToLeaveDisplay(seconds: Long?) : TimeToLeaveDisplay {
        if (seconds == null) return TimeToLeaveDisplay("nullValue", null)
        var onTime = false

        val hours = seconds / 3600
        val minutes = ((seconds % 3600) / 60)

        if (hours >= 0 && minutes >= 0) {
            onTime = true
        }
        return if (hours.toInt() != 0) {
            TimeToLeaveDisplay("${hours.absoluteValue}h${minutes.absoluteValue}m", onTime)
        } else {
            TimeToLeaveDisplay("${minutes.absoluteValue}m", onTime)
        }
    }


    /**
     * converts from seconds since Unix epoch (January 1, 1970, 00:00:00 GMT) to format of pattern "HH:mm yyyy-MM-dd"
     */
    fun unixTimeToDateTimeString(unixTime: Long?): String {
        if (unixTime == null) return ""
        val instant = Instant.ofEpochSecond(unixTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd")
        return localDateTime.format(formatter)

    }

    fun getLocalTimeInUnixTime(): Long {
        return ZonedDateTime.now().toEpochSecond()
    }

    fun unixTimeToLocalZonedDateTime(unixTime: Long): ZonedDateTime {
        val instant = Instant.ofEpochSecond(unixTime)
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    fun zonedDateTimeToShortFormat(zonedDateTime: ZonedDateTime) : String {
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(Locale.ENGLISH)
        return formatter.format(zonedDateTime)
    }

    fun googleTimeToHoursMinutes(timeString: String): String {
        val zonedDateTime = ZonedDateTime.parse(timeString)
        return zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun formatSecondsToDateTimeString(seconds: Int?): String {
        val instant = seconds?.let { Instant.ofEpochSecond(it.toLong()) }
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return localDateTime.format(formatter)
    }

    fun formatDateTimeStringToUnix(timestamp: String?): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return try {
            LocalDateTime.parse(timestamp, formatter).atZone(ZoneId.systemDefault()).toEpochSecond()
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Error parsing timestamp from Date Time String to Unix: ${e.message}")
            return 0
        }
    }
}

