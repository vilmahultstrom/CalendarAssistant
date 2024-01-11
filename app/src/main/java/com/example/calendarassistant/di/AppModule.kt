package com.example.calendarassistant.di



import android.content.Context
import com.example.calendarassistant.model.google.GoogleAuthClient
import com.example.calendarassistant.model.google.GoogleCalendar
import com.example.calendarassistant.model.services.CalendarService
import com.example.calendarassistant.model.services.NetworkService
import com.google.android.gms.auth.api.identity.Identity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        val client = Identity.getSignInClient(context)
        val googleAuthClient = GoogleAuthClient(context,client)
        return CalendarService(googleCalendar, googleAuthClient)
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