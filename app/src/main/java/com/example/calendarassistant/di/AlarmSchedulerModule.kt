package com.example.calendarassistant.di

import android.content.Context
import com.example.calendarassistant.data.AndroidAlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AlarmSchedulerModule {
    @Provides
    fun provideAndroidAlarmScheduler(@ApplicationContext context: Context): AndroidAlarmScheduler {
        return AndroidAlarmScheduler(context)
    }
}
