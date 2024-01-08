package com.example.calendarassistant.network.dto.sl.realtimeData.internal

import com.google.gson.annotations.SerializedName


data class StopInfo (

  @SerializedName("StopAreaNumber" ) var stopAreaNumber : Int?    = null,
  @SerializedName("StopAreaName"   ) var stopAreaName   : String? = null,
  @SerializedName("TransportMode"  ) var transportMode  : String? = null,
  @SerializedName("GroupOfLine"    ) var groupOfLine    : String? = null

)