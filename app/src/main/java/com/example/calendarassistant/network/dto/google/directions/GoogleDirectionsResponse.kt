package com.example.calendarassistant.network.dto.google.directions;
import com.example.calendarassistant.network.dto.google.directions.internal.GeocodedWaypoints
import com.example.calendarassistant.network.dto.google.directions.internal.Routes
import com.google.gson.annotations.SerializedName


data class GoogleDirectionsResponse (

  @SerializedName("geocoded_waypoints" ) var geocodedWaypoints : ArrayList<GeocodedWaypoints> = arrayListOf(),
  @SerializedName("routes"             ) var routes            : ArrayList<Routes>            = arrayListOf(),
  @SerializedName("status"             ) var status            : String?                      = null

)