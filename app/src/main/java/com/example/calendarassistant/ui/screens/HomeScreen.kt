package com.example.calendarassistant.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.model.BottomMenuContent
import com.example.calendarassistant.network.location.LocationService
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.BoxButton
import com.example.calendarassistant.ui.screens.components.ButtonSection
import com.example.calendarassistant.ui.screens.components.DepartureSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.screens.components.NextEventSection
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.viewmodels.TestVM

@Composable
fun HomeScreen(
    // TODO: add VM here
    vm: TestVM,
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()

    // For starting Gps tracking
    val startServiceAction by vm.startServiceAction
    startServiceAction?.getContentIfNotHandled()?.let { action ->
        LaunchedEffect(action) {
            Intent(context, LocationService::class.java).apply {
                this.action = action
                context.startService(this)
            }
        }
    }



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
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                BoxButton(
                    padding = 8.dp,
                    color = ButtonBlue,
                    onClick = vm::onStartServiceClicked,
                    buttonText = "Start/stop gps-tracking"
                )
                Text(text = "Current pos: Lat: ${uiState.latitude}, Lon: ${uiState.longitude}")

            }
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
