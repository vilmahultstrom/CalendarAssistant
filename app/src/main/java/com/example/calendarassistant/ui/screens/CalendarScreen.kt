package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.NavRoute
import com.example.calendarassistant.ui.screens.components.BottomMenuContent
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.calendar_screen.DatePickSection
import com.example.calendarassistant.ui.screens.components.calendar_screen.EventsSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.viewmodels.CalendarVM
import com.example.calendarassistant.utilities.DateHelpers

@Composable
fun CalendarScreen(
    vm: CalendarVM, navController: NavController
) {
    val state by vm.uiState.collectAsState()

    // updating calender
    LaunchedEffect(key1 = Unit){
        vm.updateCalendar()
    }

    // Saving start index
    DisposableEffect(key1 = Unit) {
        onDispose {
            vm.setStartIndex()
        }
    }



    val daysInMonth = DateHelpers.getCurrentMonthDates(state.dateOfToday)
    val events = vm.eventsWithLocation.collectAsState().value
//    LaunchedEffect(Unit) {
//        if (!vm.isUserSignedIn()) {
//            vm.clearEvents()
//        }
//    }
    //Log.d("CalenderScreen", "is Logged in: ${vm.isUserSignedIn()}")
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        Column( modifier = Modifier
            .fillMaxSize()
        ) {
            InformationSection("Daily overview", state.dateOfToday.toString(), modifier = Modifier.weight(.1f))
            DatePickSection(dates = daysInMonth, startIndex = (state.startIndex), vm = vm, modifier = Modifier.weight(.3f))
            EventsSection(events, modifier = Modifier.weight(.6f), vm=vm)
        }
        BottomMenu(
            items = listOf(
                BottomMenuContent("Home", R.drawable.baseline_home_24, NavRoute.Home.route),
                BottomMenuContent("Calendar", R.drawable.baseline_calendar_month_24, NavRoute.Calendar.route),
                BottomMenuContent("Settings", R.drawable.baseline_settings_24, NavRoute.Settings.route),
            ), modifier = Modifier.align(Alignment.BottomCenter), navController = navController
        )
    }


}





