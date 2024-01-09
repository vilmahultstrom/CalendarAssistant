package com.example.calendarassistant.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.calendarassistant.ui.viewmodels.TestVM

@Composable
fun LoginScreen(
    vm: TestVM,
    navController: NavController
) {
    Column{
        Button(onClick= {vm.login()}){
            Text(text="login")
        }
        Button(onClick= {vm.fetchEvents()}){
            Text(text="fetch events")
        }
    }
}