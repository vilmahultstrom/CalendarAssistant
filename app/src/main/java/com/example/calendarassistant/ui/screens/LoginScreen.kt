package com.example.calendarassistant.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.calendarassistant.ui.viewmodels.TestVM

@Composable
fun LoginScreen(
    // TODO: add VM here
    vm: TestVM,
    navController: NavController
) {
    Button(onClick= {vm.login()}){
        Text(text="login")
    }
}