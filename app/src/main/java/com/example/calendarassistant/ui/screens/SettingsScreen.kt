package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.TestVM

@Composable
fun SettingsScreen(
    vm: TestVM,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        Column {
            InformationSection("Settings", "Here you can sync your google account")
            Text(text = "Welcome to the Settings Screen", color = TextWhite, modifier = Modifier.padding(30.dp))
            Button(modifier = Modifier.padding(30.dp), onClick = { navController.navigate(BMRoutes.Login.route) }) {
                Text(text = "Go to Algots beautiful Login page")
            }
            // TODO: Välj vilken kalender som ska importeras / logga in?
            // TODO: Andra inställningar
        }
        BottomMenu(
            items = listOf(
                BottomMenuContent("Home", R.drawable.baseline_home_24, BMRoutes.Home.route),
                BottomMenuContent(
                    "Calendar",
                    R.drawable.baseline_calendar_month_24,
                    BMRoutes.Calendar.route
                ),
                BottomMenuContent(
                    "Settings",
                    R.drawable.baseline_settings_24,
                    BMRoutes.Settings.route
                ),
            ),
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}