package com.example.calendarassistant.network.dto.sl.realtimeData

import com.example.calendarassistant.network.dto.sl.realtimeData.internal.ResponseData
import com.google.gson.annotations.SerializedName


data class SlRealtimeDataResponse (

  @SerializedName("StatusCode"    ) var statusCode    : Int?          = null,
  @SerializedName("Message"       ) var message       : String?       = null,
  @SerializedName("ExecutionTime" ) var executionTime : Int?          = null,
  @SerializedName("ResponseData"  ) var responseData  : ResponseData? = ResponseData()

)