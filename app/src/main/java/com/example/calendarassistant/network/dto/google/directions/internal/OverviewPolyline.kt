package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class OverviewPolyline (

  @SerializedName("points" ) var points : String? = null

)