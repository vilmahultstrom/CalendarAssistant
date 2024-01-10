package com.example.calendarassistant.ui.screens.components.calendarScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendarassistant.R
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DarkViolet
import com.example.calendarassistant.ui.theme.LightGreen3
import com.example.calendarassistant.ui.theme.LightRed
import com.example.calendarassistant.ui.theme.RoyalPurple80
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun EventsSection(events: List<MockCalendarEvent>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(
                text = "TODAY",
                style = MaterialTheme.typography.headlineSmall,
                color = LightRed,
                modifier = Modifier.padding(15.dp)
            )
            Text(
                text = "${events.size} Events",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(15.dp)
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(events.size) { index ->
                val event = events[index]
                EventItem(event)
            }
        }
    }
}

@Composable
fun EventItem(event: MockCalendarEvent) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .aspectRatio(2f)
            .clip(RoundedCornerShape(10.dp))
            .background(RoyalPurple80)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Text(
                text = event.summary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ButtonBlue)
                            .padding(10.dp)
                            .clickable { /* TODO: Öppna google maps till location? */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = event.location,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .width(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ButtonBlue)
                            .padding(10.dp)
                            .clickable { /* TODO: Ställ ett larm för den tiden? */ },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_access_time_24),
                            contentDescription = event.summary,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        val formattedTime = event.start
                        Text(
                            text = formattedTime,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}