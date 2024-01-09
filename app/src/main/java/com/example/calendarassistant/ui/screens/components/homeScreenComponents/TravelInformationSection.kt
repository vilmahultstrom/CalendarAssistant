package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.model.mock.travel.Deviation
import com.example.calendarassistant.model.mock.travel.DeviationInformation
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.UiState

private const val TAG = "TravelInformationSection"

@Composable
fun TravelInformationSection(
    travelInfo: UiState
//    deviationInfo: DeviationInformation
) {
    val routeInfo = travelInfo.travelInformation
    val deviationInfo = travelInfo.transitDeviationInformation
    val stepsDeviationInfo = travelInfo.transitDeviationInformation.transitStepsDeviations

//    Row {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            Log.d(TAG, "1")
//            if (stepsDeviationInfo != null) {
//                Log.d(TAG, "2")
//                items(stepsDeviationInfo.size) { step ->
//                    StepInformationCard(stepsDeviationInfo[step])
//                    Log.d(TAG, "3  +  ${stepsDeviationInfo[step].delayInMinutes}")
//                }
//            }
//        }
//    }



    /*LazyColumn(modifier = Modifier.fillMaxSize()) {
        Log.d(TAG, "1")
        if (stepsDeviationInfo != null) {
            if (stepsDeviationInfo.isNotEmpty()) {
                Log.d(TAG, "2  +  ${stepsDeviationInfo.size}")
                items(stepsDeviationInfo.size) {step ->
                    Log.d(TAG, "3")
                    StepInformationCard(deviationInfo = stepsDeviationInfo[step])
                    Log.d(TAG, "4  +  ${stepsDeviationInfo[step].delayInMinutes}")
                }
            }
        }
    }*/

    if (stepsDeviationInfo != null) {
        Log.d(TAG, "1")
        if (stepsDeviationInfo.isNotEmpty())
            Log.d(TAG, "2  +  ${stepsDeviationInfo.size}  +   ")
            Column(modifier = Modifier.fillMaxSize()) {
                stepsDeviationInfo.forEach { step ->
                    StepInformationCard(step)
                }
            }
    }
}

@Composable
fun StepInformationCard(
    deviationInfo: DeviationInformation
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Log.d(TAG, "2.1")
            if (deviationInfo.delayInMinutes != 0) {
                Log.d(TAG, "2.2")
                Text(
                    text = "Delay: ${deviationInfo.delayInMinutes} minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextWhite
                )
            }
            Log.d(TAG, "2.3")
            deviationInfo.deviations?.forEach { deviation ->
                DeviationText(deviation = deviation)
            }
            /*deviationInfo.deviations?.forEach { deviation ->
                Log.d(TAG, "2.4  +  ${deviation.text}")
                Text(
                    text = "${deviation.importanceLevel}",
                    fontWeight = FontWeight.Medium,
                    color = Color.Blue,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = "${deviation.text}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = "${deviation.consequence}",
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }*/
        }
    }
}

@Composable
fun DeviationText(deviation: Deviation) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
            text = "${deviation.importanceLevel}",
            fontWeight = FontWeight.Medium,
            color = Color.Red
        )
        Text(
            text = "${deviation.text}",
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "${deviation.consequence}",
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}