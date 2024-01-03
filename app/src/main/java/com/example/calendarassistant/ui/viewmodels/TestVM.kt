package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.network.GoogleApi
import kotlinx.coroutines.launch

private const val TAG = "TestVm"

class TestVM() : ViewModel() {

    fun getDirectionsByPlace() {
        viewModelScope.launch {
            val response = GoogleApi.getDirectionsByPlace("Nyköping", "Stockholm", TravelMode.Transit)
            Log.d(TAG, response.body().toString())
        }
    }

    fun getDirectionsByCoordinates() {
        viewModelScope.launch {
            val response = GoogleApi.getDirectionsByCoordinates(Pair(58.75311F, 17.009333F), Pair(59.33459f, 18.063240f), TravelMode.Transit)
        }
    }
}