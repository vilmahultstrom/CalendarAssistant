package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.model.mock.travel.Deviation
import com.example.calendarassistant.model.mock.travel.DeviationInformation
import com.example.calendarassistant.ui.theme.ForestGreen
import com.example.calendarassistant.ui.theme.TextWhite
import com.example.calendarassistant.ui.viewmodels.UiState

private const val TAG = "TravelInformationSection"

@Composable
fun TravelInformationSection(
    travelInfo: UiState
//    deviationInfo: DeviationInformation
) {
//    val routeInfo = travelInfo.travelInformation
//    val deviationInfo = travelInfo.transitDeviationInformation
/*    val stepsDeviationInfo = travelInfo.transitDeviationInformation.transitStepsDeviations

    if (!stepsDeviationInfo.isNullOrEmpty()) {
        // Checks if any step has delay or deviations info
        val containsInfo = stepsDeviationInfo.any { step ->
            step.delayInMinutes != 0 || !step.deviations.isNullOrEmpty()
        }

        if (containsInfo) {
            Column(modifier = Modifier.fillMaxSize()) {
                stepsDeviationInfo.forEach { step ->
                    StepInformationCard(step)
                }
            }
        }
    }*/
}

@Composable
fun ExpandableTravelInformationSection(travelInfo: UiState) {
    var expanded by remember { mutableStateOf(false) }

    val stepsDeviationInfo = travelInfo.transitDeviationInformation.transitStepsDeviations

    if (!stepsDeviationInfo.isNullOrEmpty()) {
        // Checks if any step has delay or deviations info
        val containsInfo = stepsDeviationInfo.any { step ->
            step.delayInMinutes != 0 || !step.deviations.isNullOrEmpty()
        }

        if (containsInfo) {
            /*Column(modifier = Modifier.fillMaxSize()) {
                stepsDeviationInfo.forEach { step ->
                    StepInformationCard(step)
                }
            }*/

            Column {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(text = if (expanded) "Dölj information" else "Visa information") //TODO: sätt dessa som "variabler"
                }

                AnimatedVisibility(visible = expanded) {
                    DeviationDetailsSection(stepsDeviationInfo)
                }
            }
        }
    }
}

@Composable
fun DeviationDetailsSection(stepsDeviationInfo: List<DeviationInformation>) {
    stepsDeviationInfo.forEach { step ->
        StepInformationCard(step)
    }
}

@Composable
fun StepInformationCard(
    deviationInfo: DeviationInformation,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(8/2f),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
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
            color = TextWhite
        )
        Text(
            text = "${deviation.consequence}",
            fontWeight = FontWeight.Normal,
            color = TextWhite
        )
    }
}