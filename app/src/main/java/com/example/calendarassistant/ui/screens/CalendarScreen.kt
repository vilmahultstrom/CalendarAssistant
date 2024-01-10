package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.model.BottomMenuContent
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.calendarScreenComponents.DatePickSection
import com.example.calendarassistant.ui.screens.components.calendarScreenComponents.EventsSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.viewmodels.CalendarVM
import com.example.calendarassistant.utilities.DateHelpers
import java.time.LocalDate

@Composable
fun CalendarScreen(
    vm: CalendarVM, navController: NavController
) {
    // TODO: Get date logic from backend
    val state by vm.uiState.collectAsState()



    val daysInMonth = DateHelpers.getCurrentMonthDates(state.dateOfToday)
    // TODO: Replace with real events imported from Google Calendar?
    val events = vm.eventsWithLocation.collectAsState().value
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        Column( modifier = Modifier
            .fillMaxSize()
        ) {
            InformationSection("Daily overview", state.dateOfToday.toString(), modifier = Modifier.weight(.1f))
            DatePickSection(dates = daysInMonth, startIndex = (state.dateOfToday.dayOfMonth - 1).toString(), vm= vm, modifier = Modifier.weight(.3f))
            EventsSection(events, modifier = Modifier.weight(.6f))
        }
        BottomMenu(
            items = listOf(
                BottomMenuContent("Home", R.drawable.baseline_home_24, BMRoutes.Home.route),
                BottomMenuContent("Calendar", R.drawable.baseline_calendar_month_24, BMRoutes.Calendar.route),
                BottomMenuContent("Settings", R.drawable.baseline_settings_24, BMRoutes.Settings.route),
            ), modifier = Modifier.align(Alignment.BottomCenter), navController = navController
        )
    }
}



