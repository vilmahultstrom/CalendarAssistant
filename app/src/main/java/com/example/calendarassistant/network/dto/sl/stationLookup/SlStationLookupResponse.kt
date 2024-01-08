package com.example.calendarassistant.network.dto.sl.stationLookup

import com.example.calendarassistant.network.dto.sl.stationLookup.internal.ResponseData
import com.google.gson.annotations.SerializedName


data class SlStationLookupResponse (

  @SerializedName("StatusCode"    ) var statusCode    : Int?                    = null,
  @SerializedName("message"       ) var message       : String?                 = null,
  @SerializedName("ExecutionTime" ) var executionTime : Int?                    = null,
  @SerializedName("ResponseData"  ) var responseData  : ArrayList<ResponseData> = arrayListOf()

)