package com.example.calendarassistant.ui.screens.components.settingsScreenComponents

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.ui.viewmodels.SettingsVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmOffsetSection(
    vm: SettingsVM
) {
    val context = LocalContext.current

    var minutesText by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = minutesText,
            onValueChange = { minutesText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Negative offset (minutes) for alarms") }
            ,textStyle = TextStyle(Color.White)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                val minutes = minutesText.toLongOrNull()
                if (minutes != null) {
                    vm.setAlarmOffset(minutes)
                    Toast.makeText(context, "Alarms will now be set $minutes minutes before event times", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Illegal offset time", Toast.LENGTH_SHORT).show()
                }
                minutesText = ""
            }) {
                Text(text = "Set")
            }
        }
    }
}