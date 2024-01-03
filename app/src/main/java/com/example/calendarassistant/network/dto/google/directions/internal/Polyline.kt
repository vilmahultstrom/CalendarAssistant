package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class Polyline (

  @SerializedName("points" ) var points : String? = null

)