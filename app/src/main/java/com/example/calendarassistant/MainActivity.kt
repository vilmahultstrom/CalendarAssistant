package com.example.calendarassistant

<<<<<<< Updated upstream
=======
import android.Manifest
import android.content.pm.PackageManager
>>>>>>> Stashed changes
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
<<<<<<< Updated upstream
=======
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
>>>>>>> Stashed changes
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
<<<<<<< Updated upstream
=======
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.calendarassistant.ui.screens.SettingsScreen
>>>>>>> Stashed changes
import com.example.calendarassistant.ui.theme.CalendarAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< Updated upstream
=======

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

>>>>>>> Stashed changes
        setContent {
            CalendarAssistantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
<<<<<<< Updated upstream
                    Greeting("Android")
=======
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
                    }
>>>>>>> Stashed changes
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalendarAssistantTheme {
        Greeting("Android")
    }
}