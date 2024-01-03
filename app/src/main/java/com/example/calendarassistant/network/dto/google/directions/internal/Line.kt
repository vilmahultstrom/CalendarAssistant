package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class Line (

    @SerializedName("agencies"   ) var agencies  : ArrayList<Agencies> = arrayListOf(),
    @SerializedName("short_name" ) var shortName : String?             = null,
    @SerializedName("vehicle"    ) var vehicle   : Vehicle?            = Vehicle()

)