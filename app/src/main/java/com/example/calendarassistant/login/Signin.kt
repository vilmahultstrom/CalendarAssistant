package com.example.calendarassistant.login

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calendarassistant.R
import com.example.calendarassistant.ui.viewmodels.TestVM
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

const private val TAG= "GoogleSignIn"

/**
 * followed this tutorial:
 * https://www.youtube.com/watch?v=Zz3412C4BSA&ab_channel=CodesEasy
 * https://developers.google.com/identity/one-tap/android/get-started
 * https://developers.google.com/identity/one-tap/android/create-new-accounts#kotlin_1
 */
class Signin: AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

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

    private fun signIn() {
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
    }


}