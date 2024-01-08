package com.example.calendarassistant.ui.viewmodels

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "SettingsVm"

@HiltViewModel
class SettingsVM @Inject constructor(
    private val signInClient: SignInClient
) : ViewModel() {


    fun handleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the successful result
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        val email = credential.id
                        val googleIdToken = credential.googleIdToken
                        val displayName = credential.displayName
                        val publicKeyCredential =
                            credential.publicKeyCredential //vet inte om denna behövs. Den returnerar ändå bara null.
                        val password = credential.password
                        val givenName = credential.givenName
                        val familyName = credential.familyName
                        val profilePictureUri = credential.profilePictureUri
                        Log.d(TAG, "Got ID token.")
                        Log.d(
                            TAG,
                            "$email, $displayName, $googleIdToken, $publicKeyCredential"
                        )
                        Log.d(
                            TAG,
                            "$password, $givenName, $familyName, $profilePictureUri"
                        )

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