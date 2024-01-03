package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class DepartureTime (

  @SerializedName("text"      ) var text     : String? = null,
  @SerializedName("time_zone" ) var timeZone : String? = null,
  @SerializedName("value"     ) var value    : Int?    = null

)