package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.ButtonSection
import com.example.calendarassistant.ui.screens.components.DepartureSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.screens.components.NextEventSection
import com.example.calendarassistant.ui.theme.AquaBlue
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DarkerButtonBlue
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.theme.LightGreen2
import com.example.calendarassistant.ui.theme.Purple40
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.BottomMenuContent

@Composable
fun HomeScreen(
    // TODO: add VM here
) {
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        Column {
            InformationSection()
            NextEventSection()
            DepartureSection()
            ButtonSection()
        }
        BottomMenu(items = listOf(
            BottomMenuContent("Home", R.drawable.baseline_home_24),
            BottomMenuContent("Daily", R.drawable.baseline_calendar_today_24),
            BottomMenuContent("Weekly", R.drawable.baseline_calendar_month_24),
            BottomMenuContent("Monthly", R.drawable.baseline_construction_24),
        ), modifier = Modifier.align(Alignment.BottomCenter))
    }
}