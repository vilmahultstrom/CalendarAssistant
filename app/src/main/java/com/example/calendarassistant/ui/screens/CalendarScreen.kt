package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.model.BottomMenuContent
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.calendarScreenComponents.DatePickSection
import com.example.calendarassistant.ui.screens.components.calendarScreenComponents.EventsSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.viewmodels.TestVM
import com.example.calendarassistant.utilities.DateHelpers
import java.time.LocalDate

@Composable
fun CalendarScreen(
    vm: TestVM, navController: NavController
) {
    // TODO: Get date logic from backend
    val specificDate = LocalDate.of(2024, 1, 8)
    val daysInMonth = DateHelpers.getCurrentMonthDates(specificDate)
    // TODO: Replace with real events imported from Google Calendar?
    val events = vm.mockEvents.collectAsState().value
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        Column {
            InformationSection("Daily overview", specificDate.toString())
            DatePickSection(
                dates = daysInMonth, startIndex = (specificDate.dayOfMonth - 1).toString()
            )
            EventsSection(events)
        }
        BottomMenu(
            items = listOf(
                BottomMenuContent("Home", R.drawable.baseline_home_24, BMRoutes.Home.route),
                BottomMenuContent(
                    "Calendar", R.drawable.baseline_calendar_month_24, BMRoutes.Calendar.route
                ),
                BottomMenuContent(
                    "Settings", R.drawable.baseline_settings_24, BMRoutes.Settings.route
                ),
            ), modifier = Modifier.align(Alignment.BottomCenter), navController = navController
        )
    }
}



