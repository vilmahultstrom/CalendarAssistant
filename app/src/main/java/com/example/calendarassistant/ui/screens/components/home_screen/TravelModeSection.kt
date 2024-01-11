package com.example.calendarassistant.ui.screens.components.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.ui.theme.DeepGreen

@Composable
fun TravelModeSection(selected: TravelMode, onClick: (TravelMode) -> Unit) {
    val travelModes = listOf(TravelMode.Transit, TravelMode.Driving, TravelMode.Bicycling, TravelMode.Walking)
    val iconMap = mapOf(
        TravelMode.Transit to R.drawable.train_24px,
        TravelMode.Driving to R.drawable.directions_car_24px,
        TravelMode.Bicycling to R.drawable.pedal_bike_24px,
        TravelMode.Walking to R.drawable.directions_walk_24px
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
    ) {
        travelModes.forEach { mode ->
            TravelModeCard(
                Modifier.weight(1F),
                travelMode = mode,
                isSelected = mode == selected,
                iconId = iconMap[mode] ?: error("Icon not found"),
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelModeCard(
    modifier: Modifier,
    travelMode: TravelMode,
    isSelected: Boolean,
    iconId: Int,
    onClick: (TravelMode) -> Unit
) {
    val padding = if (isSelected) 0.dp else 10.dp
    val sizeFraction = if (isSelected) 1F else 0.8F
    val color = if (isSelected) DeepGreen else Color.LightGray

    Card(
        modifier = modifier
            .padding(padding)
            .aspectRatio(1F),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        onClick = {onClick(travelMode)}
    ) {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(sizeFraction),
                painter = painterResource(id = iconId),
                contentDescription = travelMode.name,
                tint = Color.White
            )
        }
    }
}
