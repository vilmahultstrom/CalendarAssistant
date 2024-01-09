package com.example.calendarassistant.network.dto.google.directions.internal

import com.google.gson.annotations.SerializedName


data class Steps (

  @SerializedName("distance"          ) var distance         : Distance?       = Distance(),
  @SerializedName("duration"          ) var duration         : Duration?       = Duration(),
  @SerializedName("end_location"      ) var endLocation      : EndLocation?    = EndLocation(),
  @SerializedName("html_instructions" ) var htmlInstructions : String?         = null,
  //@SerializedName("polyline"          ) var polyline         : Polyline?       = Polyline(), //TODO: Kommentera ut/ta bort som inte behövs i svaret
  @SerializedName("start_location"    ) var startLocation    : StartLocation?  = StartLocation(),
  @SerializedName("transit_details"   ) var transitDetails   : TransitDetails? = TransitDetails(),
  @SerializedName("travel_mode"       ) var travelMode       : String?         = null

)