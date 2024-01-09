package com.example.calendarassistant.login

import android.content.Context

interface GoogleAuthClientFactory {
    fun create(context: Context): GoogleAuthClient
}