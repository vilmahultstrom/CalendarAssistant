package com.example.calendarassistant.model.kth

object KthMapper {

    fun map(kthLocation: String) : String? {

        return when(kthLocation) {
            "Meitnersalen" -> "Hälsovägen 11C"
            else -> null
        }

    }



}