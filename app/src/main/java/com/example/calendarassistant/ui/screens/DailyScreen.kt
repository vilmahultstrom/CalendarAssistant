package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.model.BottomMenuContent
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.DailyScreenComponents.DatePickSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.TestVM
import com.example.calendarassistant.utilities.getCurrentMonthDates
import java.time.LocalDate

@Composable
fun DailyScreen(
    vm: TestVM,
    navController: NavController
) {
    // TODO: Get date logic from backend
    val specificDate = LocalDate.of(2024, 1, 3)
    val daysInMonth = getCurrentMonthDates(specificDate)
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        Column {
            InformationSection("Daily overview", specificDate.toString())
            DatePickSection(
                dates = daysInMonth,
                startIndex = (specificDate.dayOfMonth - 1).toString()
            )
            Text(
                text = "Welcome to the Daily Screen",
                color = TextWhite,
                modifier = Modifier.padding(30.dp)
            )
        }
        BottomMenu(
            items = listOf(
                BottomMenuContent("Home", R.drawable.baseline_home_24, BMRoutes.Home.route),
                BottomMenuContent(
                    "Daily",
                    R.drawable.baseline_calendar_today_24,
                    BMRoutes.Daily.route
                ),
                BottomMenuContent(
                    "Weekly",
                    R.drawable.baseline_calendar_month_24,
                    BMRoutes.Weekly.route
                ),
                BottomMenuContent(
                    "Monthly",
                    R.drawable.baseline_construction_24,
                    BMRoutes.Monthly.route
                ),
            ),
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

