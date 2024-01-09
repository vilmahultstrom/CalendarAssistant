package com.example.calendarassistant.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.calendarassistant.login.GoogleCalendar
import com.example.calendarassistant.services.CalendarService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "CalendarVM"

@HiltViewModel
class CalendarVM @Inject constructor(private val calendarService: CalendarService
): ViewModel() {
}