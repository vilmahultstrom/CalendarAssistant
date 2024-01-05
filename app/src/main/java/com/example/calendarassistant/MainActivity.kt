package com.example.calendarassistant

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.ui.screens.CalendarScreen
import com.example.calendarassistant.ui.screens.HomeScreen
import com.example.calendarassistant.ui.screens.LoginScreen
import com.example.calendarassistant.ui.screens.SettingsScreen
import com.example.calendarassistant.ui.theme.CalendarAssistantTheme
import com.example.calendarassistant.ui.viewmodels.TestVM
import dagger.hilt.android.AndroidEntryPoint

// TODO: Remove rotation
// TODO: Implement dependency injection
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.FOREGROUND_SERVICE
            ),
            0
        )


        setContent {
            CalendarAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val testVM = hiltViewModel<TestVM>()

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = BMRoutes.Home.route
                    ) {
                        composable(BMRoutes.Home.route) {
                            HomeScreen(vm = testVM, navController)
                        }
                        composable(BMRoutes.Calendar.route) {
                            CalendarScreen(vm = testVM, navController)
                        }
                        composable(BMRoutes.Settings.route) {
                            SettingsScreen(vm = testVM, navController)
                        }
                        composable(BMRoutes.Login.route) {
                            LoginScreen(testVM, navController)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalendarAssistantTheme {
        //Greeting("Android")
    }
}