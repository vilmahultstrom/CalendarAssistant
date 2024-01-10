package com.example.calendarassistant.network.dto.sl.nearbyStops.internal

import com.google.gson.annotations.SerializedName


data class StopLocation (

  @SerializedName("id"            ) var id            : String?  = null,
  @SerializedName("extId"         ) var extId         : String?  = null,
  @SerializedName("hasMainMast"   ) var hasMainMast   : Boolean? = null,
  @SerializedName("mainMastId"    ) var mainMastId    : String?  = null,
  @SerializedName("mainMastExtId" ) var mainMastExtId : String?  = null,
  @SerializedName("name"          ) var name          : String?  = null,
  @SerializedName("lon"           ) var lon           : Double?  = null,
  @SerializedName("lat"           ) var lat           : Double?  = null,
  @SerializedName("weight"        ) var weight        : Int?     = null,
  @SerializedName("dist"          ) var dist          : Int?     = null,
  @SerializedName("products"      ) var products      : Int?     = null

)