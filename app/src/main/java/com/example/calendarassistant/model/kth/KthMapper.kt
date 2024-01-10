package com.example.calendarassistant.model.kth

object KthMapper {

    /**
     * Maps lecture halls to address as the location from kth isn't enough
     */
    fun map(kthLocation: String): String? {

        return when (kthLocation) {
            "Meitnersalen",
            "T67, Hälsovägen",
            "T61, Hälsovägen",
            "Brånemarksalen",
            "Fl-Projektrum 3",
            "Hertzsalen" -> "Hälsovägen 11C"
            else -> null
        }

    }


}