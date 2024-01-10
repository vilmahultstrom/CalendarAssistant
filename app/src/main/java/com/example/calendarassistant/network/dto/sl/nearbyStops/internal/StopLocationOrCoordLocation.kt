package com.example.calendarassistant.network.dto.sl.nearbyStops.internal

import com.google.gson.annotations.SerializedName


data class StopLocationOrCoordLocation (

  @SerializedName("StopLocation" ) var stopLocation : StopLocation? = StopLocation()

)