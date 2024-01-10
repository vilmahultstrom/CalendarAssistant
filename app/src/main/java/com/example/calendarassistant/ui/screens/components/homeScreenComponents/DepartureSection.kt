package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.ui.theme.RoyalPurple40
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.UiState

@Composable
fun DepartureSection(
    color: Color = RoyalPurple40,
    departureInfo: List<Steps>,
    uiState: UiState,
//    onClick: () -> Unit
) {
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
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
            }
        }
        TravelInformationExpandableSection(
            travelInfo = uiState,
            departureInfo = departureInfo
        )
    }
}
