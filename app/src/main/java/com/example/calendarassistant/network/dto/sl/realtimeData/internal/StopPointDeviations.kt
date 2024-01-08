package com.example.calendarassistant.network.dto.sl.realtimeData.internal

import com.example.calendarassistant.network.dto.sl.realtimeData.internal.Deviation
import com.example.calendarassistant.network.dto.sl.realtimeData.internal.StopInfo
import com.google.gson.annotations.SerializedName


data class StopPointDeviations (

    @SerializedName("StopInfo"  ) var stopInfo  : StopInfo?  = StopInfo(),
    @SerializedName("Deviation" ) var deviation : Deviation? = Deviation()

)