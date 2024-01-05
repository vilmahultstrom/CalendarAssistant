package com.example.calendarassistant.network.dto.sl.realtimeData.internal

import com.google.gson.annotations.SerializedName


data class ResponseData (

  @SerializedName("LatestUpdate"        ) var latestUpdate        : String?                        = null,
  @SerializedName("DataAge"             ) var dataAge             : Int?                           = null,
  @SerializedName("Metros"              ) var metros              : ArrayList<Metros>              = arrayListOf(),
  @SerializedName("Buses"               ) var buses               : ArrayList<Buses>               = arrayListOf(),
  @SerializedName("Trains"              ) var trains              : ArrayList<Trains>              = arrayListOf(),
  @SerializedName("Trams"               ) var trams               : ArrayList<Trams>               = arrayListOf(),
  @SerializedName("Ships"               ) var ships               : ArrayList<String>              = arrayListOf(),
  @SerializedName("StopPointDeviations" ) var stopPointDeviations : ArrayList<StopPointDeviations> = arrayListOf()

)