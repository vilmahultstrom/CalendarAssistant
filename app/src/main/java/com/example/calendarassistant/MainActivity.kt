package com.example.calendarassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calendarassistant.ui.screens.HomeScreen
import com.example.calendarassistant.ui.theme.CalendarAssistantTheme
import androidx.navigation.compose.rememberNavController
import com.example.calendarassistant.ui.screens.DailyScreen
import com.example.calendarassistant.ui.screens.MonthlyScreen
import com.example.calendarassistant.ui.screens.WeeklyScreen

// TODO: Remove rotation
// TODO: Implement dependency injection
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: Instantiate VM

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "0"
                    ) {
                        composable("0") {
                            HomeScreen(navController)
                        }
                        composable("1") {
                            DailyScreen(navController)
                        }
                        composable("2") {
                            WeeklyScreen(navController)
                        }
                        composable("3") {
                            MonthlyScreen(navController)
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