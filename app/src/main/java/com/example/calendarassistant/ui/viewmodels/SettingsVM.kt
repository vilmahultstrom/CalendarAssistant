package com.example.calendarassistant.ui.viewmodels

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.login.CalendarGoogle
import com.example.calendarassistant.login.GoogleAuthClient
import com.example.calendarassistant.login.GoogleAuthClientFactory
import com.example.calendarassistant.login.SignInResult
import com.example.calendarassistant.login.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsVm"

@HiltViewModel
class SettingsVM @Inject constructor(
    private val googleAuthClient: GoogleAuthClient,
    private val calendarGoogle: CalendarGoogle
) : ViewModel() {

    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()



    private val _signInIntentSender = MutableSharedFlow<IntentSender?>(replay = 1)
    val signInIntentSender: SharedFlow<IntentSender?> = _signInIntentSender.asSharedFlow()

    fun signIn() {
        viewModelScope.launch {
            val intentSender = googleAuthClient.signIn()
            _signInIntentSender.emit(intentSender)
        }
    }


    fun handleSignInResult(resultCode: Int, data: Intent?) {
        viewModelScope.launch {
            if (resultCode == RESULT_OK && data != null) {
                val signInResult = googleAuthClient.getSignInResultFromIntent(data)
                Log.d(TAG, signInResult.data.toString())
                calendarGoogle.getUpcomingEvents(signInResult.data!!.email!!)
            } else {
                Log.d(TAG, "Error signing in, Result code: " + resultCode.toString() + " " + data.toString())
            }
        }
    }



    fun onSignInResult(result: SignInResult) {
        _signInState.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _signInState.update { SignInState() }
    }




}