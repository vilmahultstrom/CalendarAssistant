package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.model.travel.Deviation
import com.example.calendarassistant.model.travel.DeviationData
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.ui.theme.DeepPurple100
import com.example.calendarassistant.ui.theme.DeepPurple50
import com.example.calendarassistant.ui.theme.DeepPurple500
import com.example.calendarassistant.ui.theme.Red700
import com.example.calendarassistant.ui.theme.Red900
import com.example.calendarassistant.ui.theme.LightRed
import com.example.calendarassistant.ui.theme.RoyalPurple40
import com.example.calendarassistant.ui.viewmodels.UiState

private const val TAG = "TravelInformationSection"

@Composable
fun TravelInformationExpandableSection(
    color: Color = RoyalPurple40,
    travelInfo: UiState,
    departureInfo: List<Steps>
) {
    /*    var expanded by remember { mutableStateOf(false) }

    val stepsDeviationInfo = travelInfo.transitDeviationInformation.transitStepsDeviations

    if (!stepsDeviationInfo.isNullOrEmpty()) {
        // Checks if any step has delay or deviations info
        val containsInfo = stepsDeviationInfo.any { step ->
            step.delayInMinutes != 0 || !step.deviations.isNullOrEmpty()
        }

        if (containsInfo) {

            Column(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(text = if (expanded) "Dölj information" else "Visa information") //TODO: sätt dessa som "variabler"
                }

                AnimatedVisibility(visible = expanded) {
                    departureInfo.forEach { step ->
                        TravelStepCard(
                            departureTime = step.transitDetails?.departureTime?.text,
                            arrivalTime = step.transitDetails?.arrivalTime?.text,
                            originName = step.transitDetails?.departureStop?.name,
                            stopName = step.transitDetails?.arrivalStop?.name,
                            lineName = step.transitDetails?.line?.shortName,
                        )
                        DeviationDetailsSection(stepsDeviationInfo)
                    }

                }
            }
        }
    }*/


    var expanded by remember { mutableStateOf(false) }

    val stepsDeviationInfo = travelInfo.transitDeviationData.transitStepsDeviations ?: listOf()


    if (stepsDeviationInfo.isNotEmpty() || departureInfo.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
        ) {
            TextButton(
                onClick = { expanded = !expanded },
            ) {
                Text(
                    text = if (expanded) "Hide information" else "Show information",
                    color = DeepPurple50
                )
            }

            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max)
                ) {
                    departureInfo.forEachIndexed { index, step ->
                        TravelStepCard(
                            departureTime = step.transitDetails?.departureTime?.text,
                            arrivalTime = step.transitDetails?.arrivalTime?.text,
                            originName = step.transitDetails?.departureStop?.name,
                            stopName = step.transitDetails?.arrivalStop?.name,
                            lineName = step.transitDetails?.line?.shortName,
                        )

                        if (index < stepsDeviationInfo.size) {
                            val containsInfo = isContainingInfo(stepsDeviationInfo[index])
                            if (containsInfo) {
                                StepDeviationCard(deviationInfo = stepsDeviationInfo[index])
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun isContainingInfo(deviationInfo: DeviationData) : Boolean {
    if (!deviationInfo.deviations.isNullOrEmpty()) {
        // Checks if any step has delay or deviations info
        return deviationInfo.deviations.any { item ->
            item.importanceLevel != 0 || item.text.isNullOrEmpty()
                    || !item.consequence.isNullOrEmpty()
        }
    }
    return false
}

@Composable
fun TravelStepCard(
    departureTime: String?,
    arrivalTime: String?,
    originName: String?,
    stopName: String?,
    lineName: String?,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = DeepPurple100
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.2f)
            ) {
                if (departureTime != null) {
                    Text(
                        text = departureTime,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple500
                    )
                }
                if (arrivalTime != null) {
                    Text(
                        text = arrivalTime,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple500
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(0.7f)
            ) {
                if (originName != null) {
                    Text(
                        text = originName,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple500,
                    )
                }
                if (stopName != null) {
                    Text(
                        text = stopName,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple500,
                    )
                }
            }
            if (lineName != null) {
                Text(
                    text = lineName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepPurple500,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.1f)
                )
            }
        }
    }
}

@Composable
fun StepDeviationCard(
    deviationInfo: DeviationData,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 0.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DeepPurple100
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            if (deviationInfo.delayInMinutes != 0) {
                Text(
                    text = "Delay: ${deviationInfo.delayInMinutes} minutes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Red900,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            if (!deviationInfo.deviations.isNullOrEmpty()) {
                // Checks if any step has delay or deviations info
                val containsInfo = deviationInfo.deviations.any { item ->
                    item.importanceLevel != 0 || item.text.isNullOrEmpty()
                            || !item.consequence.isNullOrEmpty()
                }
                if (containsInfo) {
                    deviationInfo.deviations.forEach { deviation ->
                        DeviationText(deviation = deviation)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviationText(
    deviation: Deviation
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        /*Text(
            text = "${deviation.importanceLevel}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = LightRed
        )*/
        Text(
            text = "${deviation.consequence}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = DeepPurple500
        )
        Text(
            text = "${deviation.text}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = DeepPurple500,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }
}