package com.example.calendarassistant.enums

enum class TravelMode {
    Transit, Driving, Walking, Bicycling;

    override fun toString(): String {
        return when (this) {
            Transit -> "transit"
            Driving -> "driving"
            Walking -> "walking"
            Bicycling -> "bicycling"
        }
    }
}