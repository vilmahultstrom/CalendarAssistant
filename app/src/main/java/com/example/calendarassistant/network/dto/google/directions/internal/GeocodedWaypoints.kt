package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class GeocodedWaypoints (

  @SerializedName("geocoder_status" ) var geocoderStatus : String?           = null,
  @SerializedName("place_id"        ) var placeId        : String?           = null,
  @SerializedName("types"           ) var types          : ArrayList<String> = arrayListOf()

)