package com.example.calendarassistant.network.dto.sl.stationLookup.internal

import com.google.gson.annotations.SerializedName


data class ResponseData (

  @SerializedName("Name"     ) var name     : String? = null,
  @SerializedName("SiteId"   ) var siteId   : String? = null,
  @SerializedName("Type"     ) var type     : String? = null,
  @SerializedName("X"        ) var x        : String? = null,
  @SerializedName("Y"        ) var y        : String? = null,
  @SerializedName("Products" ) var products : String? = null

)