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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.ui.screens.CalendarScreen
import com.example.calendarassistant.ui.screens.HomeScreen
import com.example.calendarassistant.ui.screens.LoginScreen
import com.example.calendarassistant.ui.screens.SettingsScreen
import com.example.calendarassistant.ui.theme.CalendarAssistantTheme
import com.example.calendarassistant.ui.viewmodels.SettingsVM
import com.example.calendarassistant.ui.viewmodels.TestVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

// TODO: Remove rotation
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var signInLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var settingsVM: SettingsVM


    override fun onCreate(savedInstanceState: Bundle?) {

        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            settingsVM.handleSignInResult(result.resultCode, result.data)
        }

        super.onCreate(savedInstanceState)

       /* val googleAuthClient by lazy {
            GoogleAuthClient(
                context = applicationContext,
                signInClient = Identity.getSignInClient(applicationContext)
            )
        }*/

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

                            SettingsScreen(vm = settingsVM, navController, onSignInClick = {
                                settingsVM.signIn() // Trigger sign-in from the ViewModel
                            })

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