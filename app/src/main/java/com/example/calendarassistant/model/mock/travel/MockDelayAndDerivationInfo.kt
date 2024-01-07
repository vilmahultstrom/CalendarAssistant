package com.example.calendarassistant.model.mock.travel


data class DelayAndDeviationInfo(
    val delayInMinutes: Int,
    val deviations: List<Deviation>? = emptyList()
)

data class Deviation(
    val text: String,
    val consequence: String,
    val importanceLevel: Int
)
