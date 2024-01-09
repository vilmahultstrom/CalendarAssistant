package com.example.calendarassistant.di

import android.content.Context
import com.example.calendarassistant.login.GoogleAuthClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthModule {


    @Provides
    fun provideSignInClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }



    @Provides
    fun provideGoogleAuthClient(
        @ApplicationContext context: Context,
        client: SignInClient
    ): GoogleAuthClient {
        return GoogleAuthClient(context, client)
    }
}