package com.example.calendarassistant.di

import com.example.calendarassistant.services.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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





}