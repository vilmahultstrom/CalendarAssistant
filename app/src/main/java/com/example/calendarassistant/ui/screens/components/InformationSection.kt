package com.example.calendarassistant.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.ui.theme.Gothica1Bold
import com.example.calendarassistant.ui.theme.Roboto
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun InformationSection(
    greeting: String = "Hello, User", // TODO: Sync with logged in user's name
    secondGreeting: String = "I hope you're not late today", // TODO: Add random fun messages (prio 4)
    modifier: Modifier = Modifier
    ) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = secondGreeting,
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_sentiment_satisfied_24),
            contentDescription = "Search",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}