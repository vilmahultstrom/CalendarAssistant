package com.example.calendarassistant.network.dto.sl.realtimeData.internal

import com.google.gson.annotations.SerializedName


data class Ships (

  @SerializedName("TransportMode"        ) var transportMode        : String? = null,
  @SerializedName("LineNumber"           ) var lineNumber           : String? = null,
  @SerializedName("Destination"          ) var destination          : String? = null,
  @SerializedName("JourneyDirection"     ) var journeyDirection     : Int?    = null,
  @SerializedName("GroupOfLine"          ) var groupOfLine          : String? = null,
  @SerializedName("StopAreaName"         ) var stopAreaName         : String? = null,
  @SerializedName("StopAreaNumber"       ) var stopAreaNumber       : Int?    = null,
  @SerializedName("StopPointNumber"      ) var stopPointNumber      : Int?    = null,
  @SerializedName("StopPointDesignation" ) var stopPointDesignation : String? = null,
  @SerializedName("TimeTabledDateTime"   ) var timeTabledDateTime   : String? = null,
  @SerializedName("ExpectedDateTime"     ) var expectedDateTime     : String? = null,
  @SerializedName("DisplayTime"          ) var displayTime          : String? = null,
  @SerializedName("JourneyNumber"        ) var journeyNumber        : Int?    = null,
  @SerializedName("Deviations"           ) var deviations           : String? = null

)