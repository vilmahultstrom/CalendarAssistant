package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.travel.TravelInformation
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DarkViolet
import com.example.calendarassistant.ui.theme.Purple40
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun NextEventSection(
    color: Color = DarkViolet,
    onClick: () -> Unit,
    travelInformation: TravelInformation,
    nextEventInfo: MockCalendarEvent
) {

    val departureTimeHHMM = travelInformation.departureTimeHHMM.hhmmDisplay
    val isOnTime = travelInformation.departureTimeHHMM.onTime




    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 15.dp, vertical = 20.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(0.7F)) {
            Text(
                text = "Next event: " + nextEventInfo.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite
            )
            Text(

                text = when (isOnTime) {
                    true -> "Leave in $departureTimeHHMM\nat ${travelInformation.departureTime}" //
                    false -> "You are $departureTimeHHMM late"
                    else -> "" // isontime is null
                },
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .shadow(8.dp, CircleShape)
                .background(ButtonBlue)
                .clickable { onClick() }
                .padding(10.dp)

        ) {

            Icon(
                painter = painterResource(id = R.drawable.baseline_navigation_24),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}