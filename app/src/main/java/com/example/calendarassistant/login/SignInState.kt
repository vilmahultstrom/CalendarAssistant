package com.example.calendarassistant.login

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)