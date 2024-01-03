package com.example.calendarassistant.ui.screens.components.HomeScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.Pink40
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun ButtonSection(
    color: Color = Pink40
) {
    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 15.dp, vertical = 50.dp)
            .fillMaxWidth()
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Placeholder buttons", color = TextWhite)
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(ButtonBlue, shape = RoundedCornerShape(8.dp))
                        .clickable { /* Handle click */ }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "I was late", color = TextWhite)
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(ButtonBlue, shape = RoundedCornerShape(8.dp))
                        .clickable { /* Handle click */ }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "I arrived in time", color = TextWhite)
                }
            }
        }
    }
}