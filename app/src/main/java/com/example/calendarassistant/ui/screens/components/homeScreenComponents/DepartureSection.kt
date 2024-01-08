package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.model.mock.travel.DeviationInformation
import com.example.calendarassistant.model.mock.travel.TransitDeviationInformation
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DarkAmber
import com.example.calendarassistant.ui.theme.LightGreen2
import com.example.calendarassistant.ui.theme.OrangeYellow3
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun DepartureSection(
    color: Color = DarkAmber,
    departureInfo: List<Steps>,
    deviationInfo: TransitDeviationInformation,
    onClick: () -> Unit
) {
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
        Column {
            Text(
                text = "Next departure",
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                departureInfo.first().transitDetails?.departureTime?.text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                departureInfo.first().transitDetails?.departureStop?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                departureInfo.first().transitDetails?.line?.shortName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                deviationInfo.transitStepsDeviations?.forEach {deviation ->
                    Text(
                        text = "Delay: ${deviation.delayInMinutes} minutes",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .shadow(8.dp, CircleShape)
                .background(ButtonBlue)
                .clickable { onClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = "Info",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}


