package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class Vehicle (

  @SerializedName("icon" ) var icon : String? = null,
  @SerializedName("name" ) var name : String? = null,
  @SerializedName("type" ) var type : String? = null

)