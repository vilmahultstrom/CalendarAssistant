package com.example.calendarassistant.network.dto.sl.realtimeData.internal

import com.google.gson.annotations.SerializedName


data class Deviations (

  @SerializedName("Text"            ) var text            : String? = null,
  @SerializedName("Consequence"     ) var consequence     : String? = null,
  @SerializedName("ImportanceLevel" ) var importanceLevel : Int?    = null

)