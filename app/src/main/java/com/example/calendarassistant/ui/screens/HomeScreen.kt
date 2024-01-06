package com.example.calendarassistant.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.calendarassistant.ui.screens.components.homeScreenComponents.ButtonSection
import com.example.calendarassistant.ui.screens.components.homeScreenComponents.DepartureSection
import com.example.calendarassistant.ui.screens.components.homeScreenComponents.NextEventSection
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.viewmodels.TestVM
import com.example.calendarassistant.utilities.Event
private const val TAG = "HomeScreen"
@Composable
fun HomeScreen(
    vm: TestVM,
    navController: NavController
) {
    // For starting Gps tracking
    val context = LocalContext.current
    val startServiceAction by vm.startServiceAction
    gpsTracking(context, startServiceAction)
    val nextEventInfo by vm.mockEvents.collectAsState()
    val departureInfo by vm.transitSteps.collectAsState()

    val uiState by vm.uiState.collectAsState()
    val destCoordinates = uiState.travelInformation.destinationCoordinates
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {

        Column {
            InformationSection()
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)) {

                NextEventSection(onClick = { openGoogleMaps(context, destCoordinates.first,
                    destCoordinates.second
                ) }, travelInformation = uiState.travelInformation, nextEventInfo = nextEventInfo.first())
                if (departureInfo.isNotEmpty()){ // TODO: Kanske ändra detta
                    DepartureSection(departureInfo = departureInfo)
                }
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
                    Text(text = "Current pos: Lat: ${uiState.currentLatitude}, Lon: ${uiState.currentLongitude}")

                }
                Spacer(modifier = Modifier.height(200.dp))
            }


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
private fun openGoogleMaps(context: Context, latitude: Double?, longitude: Double?) {
    if (latitude == null || longitude == null) return
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${latitude},${longitude}")))
    } catch (e: ActivityNotFoundException){
        Log.d(TAG, "Google Maps not installed on device, opening browser.")
        //"http://maps.google.com/maps?saddr=${startLatitude},${startLongitude}&daddr=${latitude},${longitude}"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=${latitude},${longitude})")))
    }

}

private fun gpsTracking(context: Context, startServiceAction: Event<String>?) {
    startServiceAction?.getContentIfNotHandled()?.let { action ->
        Intent(context, LocationService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}
