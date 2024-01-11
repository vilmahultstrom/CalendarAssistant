package com.example.calendarassistant.utilities

object KthMapper {

    /**
     * Maps lecture halls to address as the location from kth isn't enough
     * In the future, let users map places to addresses that's not found in Google Maps
     */
    fun map(kthLocation: String): String? {

        return when (kthLocation) {
            "Meitnersalen",
            "T67, Hälsovägen",
            "T61, Hälsovägen",
            "Brånemarksalen",
            "Fl-Projektrum 3",
            "T3 (Meitnersalen)",
            "Hertzsalen" -> "Hälsovägen 11C"
            else -> null
        }

    }


}