package com.example.calendarassistant.login

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity

class GoogleAuthClientFactoryImpl : GoogleAuthClientFactory {
    override fun create(context: Context): GoogleAuthClient {
        val signInClient = Identity.getSignInClient(context)
        return GoogleAuthClient(context, signInClient)
    }
}