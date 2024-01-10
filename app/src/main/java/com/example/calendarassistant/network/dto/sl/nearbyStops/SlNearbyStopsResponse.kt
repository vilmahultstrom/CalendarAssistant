package com.example.calendarassistant.network.dto.sl.nearbyStops

import com.example.calendarassistant.network.dto.sl.nearbyStops.internal.StopLocationOrCoordLocation
import com.google.gson.annotations.SerializedName


data class SlNearbyStopsResponse (

  @SerializedName("stopLocationOrCoordLocation" ) var stopLocationOrCoordLocation : ArrayList<StopLocationOrCoordLocation> = arrayListOf(),
  @SerializedName("serverVersion"               ) var serverVersion               : String?                                = null,
  @SerializedName("dialectVersion"              ) var dialectVersion              : String?                                = null,
  @SerializedName("requestId"                   ) var requestId                   : String?                                = null

)