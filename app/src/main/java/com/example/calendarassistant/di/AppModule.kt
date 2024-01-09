package com.example.calendarassistant.di


import android.content.Context
import com.example.calendarassistant.di.AuthModule.provideGoogleCalendar
import com.example.calendarassistant.login.GoogleAuthClient
import com.example.calendarassistant.login.GoogleCalendar
import com.example.calendarassistant.login.Signin
import com.example.calendarassistant.services.CalendarService
import com.example.calendarassistant.services.NetworkService
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    // Example on di, accessible in di-constructors
    @Singleton
    @Provides
    fun provideNetworkService() : NetworkService {
        return NetworkService()
    }

    @Singleton
    @Provides
    fun provideCalendarService(@ApplicationContext context: Context): CalendarService {
        val googleCalendar = GoogleCalendar(context)
        return CalendarService(googleCalendar)
    }
    /*
    @Singleton
    @Provides
    fun provideCalendarService(googleCalendar: GoogleCalendar): CalendarService {
        return CalendarService(googleCalendar)
    }

     */




    //@Singleton
    //@Provides
    //fun provideSignIn() : Signin {
    //    return Signin()
    //}





}