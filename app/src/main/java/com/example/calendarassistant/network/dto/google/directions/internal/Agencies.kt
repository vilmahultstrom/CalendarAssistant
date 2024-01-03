package com.example.calendarassistant.network.dto.google.directions.internal
import com.google.gson.annotations.SerializedName


data class Agencies (

  @SerializedName("name" ) var name : String? = null,
  @SerializedName("url"  ) var url  : String? = null

)