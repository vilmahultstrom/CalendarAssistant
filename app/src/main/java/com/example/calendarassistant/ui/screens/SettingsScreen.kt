package com.example.calendarassistant.ui.screens

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import android.Manifest
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.model.BottomMenuContent
import com.example.calendarassistant.ui.screens.components.BottomMenu
import com.example.calendarassistant.ui.screens.components.InformationSection
import com.example.calendarassistant.ui.screens.components.settingsScreenComponents.GoogleSignInButton
import com.example.calendarassistant.ui.screens.components.settingsScreenComponents.NotificationSettingsSection
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.SettingsVM
import com.example.calendarassistant.ui.viewmodels.TestVM

@Composable
fun SettingsScreen(
    vm: SettingsVM,
    navController: NavController,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {

        val state by vm.signInState.collectAsState()

        val context = LocalContext.current
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
            InformationSection("Settings", "Here you can sync your google account")
            Column (
                modifier = Modifier
                    .padding(30.dp)
            ) {
                Text(text = "Welcome to the Settings Screen", color = TextWhite)

                // TODO: Get "Sign in with Google" or "Sign out" from VM depending on state
                // TODO: Open Google sign in intent
                val googleButtonText = "Sign in with Google"
                GoogleSignInButton(googleButtonText) { onSignInClick() }
                NotificationSettingsSection(vm)
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