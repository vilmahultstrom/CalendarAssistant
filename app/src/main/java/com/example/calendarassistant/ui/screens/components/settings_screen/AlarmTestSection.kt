package com.example.calendarassistant.ui.screens.components.settings_screen

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
import com.example.calendarassistant.model.alarm.AndroidAlarmScheduler
import com.example.calendarassistant.model.alarm.AlarmItem
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTestSection(

) {
    val scheduler = AndroidAlarmScheduler(LocalContext.current) // TODO: Move to VM
    var alarmItem: AlarmItem? = null

    var secondsText by remember {
        mutableStateOf("")
    }
    var title by remember {
        mutableStateOf("")
    }
    var message by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = secondsText,
            onValueChange = { secondsText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Trigger alarm in x seconds") }
            ,textStyle = TextStyle(Color.White)
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Title") },
            textStyle = TextStyle(Color.White)
        )
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Message") },
            textStyle = TextStyle(Color.White)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                alarmItem = AlarmItem(
                    time = LocalDateTime.now().plusSeconds(secondsText.toLong()), // TODO: Move to VM and calculate depending on next event?
                    title = title,
                    message = message
                )
                alarmItem?.let(scheduler::schedule)
                secondsText = ""
                title = ""
                message = ""
            }) {
                Text(text = "Schedule")
            }
            Button(onClick = {
                alarmItem?.let(scheduler::cancel)
            }) {
                Text(text = "Cancel")
            }
        }
    }
}