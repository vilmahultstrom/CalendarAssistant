package com.example.calendarassistant.login

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calendarassistant.R
import com.example.calendarassistant.enums.BMRoutes
import com.example.calendarassistant.ui.screens.LoginScreen
import com.example.calendarassistant.ui.theme.CalendarAssistantTheme
import com.example.calendarassistant.ui.viewmodels.TestVM
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

const private val TAG= "GoogleSignIn"

/**
 * followed this tutorial:
 * https://www.youtube.com/watch?v=Zz3412C4BSA&ab_channel=CodesEasy
 * https://developers.google.com/identity/one-tap/android/get-started
 * https://developers.google.com/identity/one-tap/android/create-new-accounts#kotlin_1
 */
@AndroidEntryPoint
class Signin: AppCompatActivity(), SignInInterface {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>


    fun testInit(){
        Log.d(TAG, "inside testInit")
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        //använder denna istället för
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the successful result
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            var email: String = credential.id
                            var googleIdToken = credential.googleIdToken
                            var displayName = credential.displayName
                            var publicKeyCredential = credential.publicKeyCredential //vet inte om denna behövs
                            Log.d(TAG, "Got ID token.")
                            Log.d(TAG,email + ", " +displayName+ ", "+ googleIdToken +", " + publicKeyCredential)
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "inside oncreate")
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        oneTapClient = Identity.getSignInClient(this)
        Log.d(TAG, "oneTapClient is not null: " + (oneTapClient!=null).toString())
        Log.d(TAG, "onetapClient string: "+oneTapClient.toString())
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        //använder denna istället för
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the successful result
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            var email: String = credential.id
                            var googleIdToken = credential.googleIdToken
                            var displayName = credential.displayName
                            var publicKeyCredential = credential.publicKeyCredential //vet inte om denna behövs. Den returnerar ändå bara null.
                            var password = credential.password
                            var givenName = credential.givenName
                            var familyName = credential.familyName
                            var profilePictureUri = credential.profilePictureUri
                            Log.d(TAG, "Got ID token.")
                            Log.d(TAG,email + ", " +displayName+ ", "+ googleIdToken +", " + publicKeyCredential)
                            Log.d(TAG,password + ", " +givenName+ ", "+ familyName +", " + profilePictureUri)

                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
        setContent {
            CalendarAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val testVM = hiltViewModel<TestVM>()
                    testVM.setSignInAttemptListener(this)

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = BMRoutes.Login.route
                    ) {
                        composable(BMRoutes.Login.route) {
                            LoginScreen(testVM, navController)
                        }
                    }
                }
            }
        }
    }

    override fun attemptSignIn() {
        //testInit()
        Log.d(TAG, "inside attemptSignIn")
        Log.d(TAG, "oneTapClient is not null: " + (oneTapClient!=null).toString())
        Log.d(TAG, "onetapClient string: "+oneTapClient.toString())
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    var intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    activityResultLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) { //catch kanske inte behövs
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }
        //TODO: if successful return to MainActivity. send data between activities.
    }


}