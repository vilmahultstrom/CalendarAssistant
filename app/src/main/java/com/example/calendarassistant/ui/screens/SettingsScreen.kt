package com.example.calendarassistant.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.calendarassistant.enums.NavRoute
import com.example.calendarassistant.ui.screens.components.BottomMenuContent
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.screens.components.settings_screen.AlarmOffsetSection
import com.example.calendarassistant.ui.screens.components.settings_screen.SettingButton
import com.example.calendarassistant.ui.screens.components.settings_screen.NotificationSettingsSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.viewmodels.SettingsVM

@Composable
fun SettingsScreen(
    vm: SettingsVM,
    navController: NavController,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {

        val state by vm.signInState.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(state) {
            Log.d("SettingsScreen", "LaunchedEffect triggered with state: $state")

            if (state.isSignInSuccessful) {
                Log.d("SettingsScreen", "Sign-in successful, navigating to Home route")
                navController.navigate(NavRoute.Home.route)
            } else {
                Log.d("SettingsScreen", "Sign-in not successful yet")
            }
        }
        LaunchedEffect(key1 = state.signInError) {
            state.signInError?.let { error ->
                Toast.makeText(
                    context,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Column {
            if (vm.isUserSignedIn()) {
                InformationSection("Settings", "Here you can manage your settings")
            } else {
                InformationSection("Calender Assistant", "Please sign in below")
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                ,
            ) {
                Log.d("CalenderScreen", "is Logged in: ${vm.isUserSignedIn()}")
                if (vm.isUserSignedIn()) {
                    val googleButtonText2 = "Sign out"
                    SettingButton(
                        text = googleButtonText2,
                        onClick = {
                            onSignOutClick()
                            navController.navigate(NavRoute.Settings.route)
                        },
                        painterId = R.drawable.google_g_logo
                    )
                    AlarmOffsetSection(vm)
                } else {
                    val googleButtonText = "Sign in with Google"
                    SettingButton(
                        text = googleButtonText,
                        onClick = {
                            onSignInClick()
                        },
                        painterId = R.drawable.google_g_logo
                    )
                }
                NotificationSettingsSection(vm)
                //AlarmTestSection() // Dev UI
            }

            // TODO: Välj vilken kalender som ska importeras / logga in?
            // TODO: Andra inställningar
        }
        if(vm.isUserSignedIn()) {
            BottomMenu(
                items = listOf(
                    BottomMenuContent("Home", R.drawable.baseline_home_24, NavRoute.Home.route),
                    BottomMenuContent(
                        "Calendar",
                        R.drawable.baseline_calendar_month_24,
                        NavRoute.Calendar.route
                    ),
                    BottomMenuContent(
                        "Settings",
                        R.drawable.baseline_settings_24,
                        NavRoute.Settings.route
                    ),
                ),
                modifier = Modifier.align(Alignment.BottomCenter),
                navController = navController
            )
        }
    }
}



