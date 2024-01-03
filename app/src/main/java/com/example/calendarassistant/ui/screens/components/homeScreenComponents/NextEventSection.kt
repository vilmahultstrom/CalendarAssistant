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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.Purple40
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun NextEventSection(
    color: Color = Purple40,
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
                text = "Next event",
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite
            )
            Text(
                // TODO: Get string from backend with calculated time estimation
                text = "Walk in 5m / at 10:45",
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(ButtonBlue)
                .padding(10.dp).clickable {onClick()}
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