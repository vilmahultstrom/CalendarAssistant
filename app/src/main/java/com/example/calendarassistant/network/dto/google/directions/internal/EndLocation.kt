package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class EndLocation (

  @SerializedName("lat" ) var lat : Double? = null,
  @SerializedName("lng" ) var lng : Double? = null

)