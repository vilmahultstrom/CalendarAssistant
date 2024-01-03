package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class TransitDetails (

    @SerializedName("arrival_stop"   ) var arrivalStop   : ArrivalStop?   = ArrivalStop(),
    @SerializedName("arrival_time"   ) var arrivalTime   : ArrivalTime?   = ArrivalTime(),
    @SerializedName("departure_stop" ) var departureStop : DepartureStop? = DepartureStop(),
    @SerializedName("departure_time" ) var departureTime : DepartureTime? = DepartureTime(),
    @SerializedName("headsign"       ) var headsign      : String?        = null,
    @SerializedName("line"           ) var line          : Line?          = Line(),
    @SerializedName("num_stops"      ) var numStops      : Int?           = null

)