package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import com.example.calendarassistant.utilities.DateHelpers



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.model.CalendarEvent
import com.example.calendarassistant.ui.theme.DeepPink
import com.example.calendarassistant.ui.theme.LightRed
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun NextEventSection(
    color: Color = DeepPink,
    nextEventInfo: CalendarEvent?
) {

    val summary = nextEventInfo?.summary ?: ""
    val dateTime = DateHelpers.unixTimeToDateTimeString(nextEventInfo?.startDateTime)
    val location = nextEventInfo?.location ?: ""

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(0.7F)
        ) {
            Text(text = "Next event", style = MaterialTheme.typography.headlineSmall, color = LightRed)
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_description_24),
                    contentDescription = "icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite
                )
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_location_on_24),
                    contentDescription = "icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                    contentDescription = "icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = dateTime,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite
                )
            }

        }
    }
}