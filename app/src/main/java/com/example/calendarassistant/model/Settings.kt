package com.example.calendarassistant.model

import java.time.LocalDateTime

data class Settings(
    val arrivalBufferTime: Int? = 10,
    val alarm: LocalDateTime? = null,

)
