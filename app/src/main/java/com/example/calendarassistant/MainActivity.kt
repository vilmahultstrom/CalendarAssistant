package com.example.calendarassistant

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calendarassistant.enums.NavRoute
import com.example.calendarassistant.model.google.GoogleAuthClient
import com.example.calendarassistant.ui.screens.CalendarScreen
import com.example.calendarassistant.ui.screens.HomeScreen
import com.example.calendarassistant.ui.screens.SettingsScreen
import com.example.calendarassistant.ui.theme.CalendarAssistantTheme
import com.example.calendarassistant.ui.viewmodels.CalendarVM
import com.example.calendarassistant.ui.viewmodels.HomeVM
import com.example.calendarassistant.ui.viewmodels.SettingsVM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var signInLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var settingsVM: SettingsVM
    @Inject lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {

        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            settingsVM.handleSignInResult(result.resultCode, result.data)
        }

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

        val isLoggedIn = googleAuthClient.getSignedInUser() != null

        setContent {
            CalendarAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val homeVM = hiltViewModel<HomeVM>()
                    val calendarVM = hiltViewModel<CalendarVM>()
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = if(isLoggedIn) NavRoute.Home.route else NavRoute.Settings.route
                    ) {
                        composable(NavRoute.Home.route) {
                            HomeScreen(vm = homeVM, navController)
                        }
                        composable(NavRoute.Calendar.route) {
                            CalendarScreen(vm = calendarVM, navController)
                        }
                        composable(NavRoute.Settings.route) {
                            settingsVM = hiltViewModel<SettingsVM>()
                            LaunchedEffect(settingsVM.signInIntentSender) { // or a specific key if needed
                                settingsVM.signInIntentSender.collect { intentSender ->
                                    intentSender?.let {
                                        signInLauncher.launch(
                                            IntentSenderRequest.Builder(it).build()
                                        )
                                    }
                                }
                            }
                            SettingsScreen(
                                vm = settingsVM,
                                navController,
                                onSignInClick = {
                                settingsVM.signIn() // Trigger sign-in from the ViewModel
                                },
                                onSignOutClick = {
                                    settingsVM.signOut()
                                })

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