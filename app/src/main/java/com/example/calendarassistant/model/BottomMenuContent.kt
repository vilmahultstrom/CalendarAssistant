package com.example.calendarassistant.model

import androidx.annotation.DrawableRes

// TODO: Move this class somewhere that fits
data class BottomMenuContent(
    val title: String,
    @DrawableRes val iconId: Int,
    val route: String
)
