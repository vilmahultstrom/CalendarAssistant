package com.example.calendarassistant.ui.screens.components.homeScreenComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.R
import com.example.calendarassistant.model.mock.travel.Deviation
import com.example.calendarassistant.model.mock.travel.DeviationInformation
import com.example.calendarassistant.network.dto.google.directions.internal.DepartureTime
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.ui.theme.DarkAmber
import com.example.calendarassistant.ui.theme.LightRed
import com.example.calendarassistant.ui.theme.OrangeYellow1
import com.example.calendarassistant.ui.theme.RoyalPurple40
import com.example.calendarassistant.ui.theme.RoyalPurple80
import com.example.calendarassistant.ui.viewmodels.UiState

private const val TAG = "TravelInformationSection"

/**
 * TODO: Ni får ändra hur mycket som helst i denna!! Låt kreativiteten flöda! Min tog slut...
 *      OBS! Deviation-data som visas här är hårdkodad i NetworkService.
 */


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
    val stepsDeviationInfo = travelInfo.transitDeviationInformation.transitStepsDeviations ?: listOf()

    if (stepsDeviationInfo.isNotEmpty() || departureInfo.isNotEmpty()) {
        Column(
            modifier = Modifier
//                .padding(15.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .background(color)
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
        ) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(text = if (expanded) "Hide information" else "Show information")
            }

            AnimatedVisibility(visible = expanded) {
                departureInfo.forEachIndexed { index, step ->
                    Column(modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max)) {

                        TravelStepCard(
                            departureTime = step.transitDetails?.departureTime?.text,
                            arrivalTime = step.transitDetails?.arrivalTime?.text,
                            originName = step.transitDetails?.departureStop?.name,
                            stopName = step.transitDetails?.arrivalStop?.name,
                            lineName = step.transitDetails?.line?.shortName,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (index < stepsDeviationInfo.size) {
                            StepDeviationCard(stepsDeviationInfo[index])
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
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
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = RoyalPurple80)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                if (departureTime != null) {
                    Text(text = departureTime)
                }
                if (arrivalTime != null) {
                    Text(text = arrivalTime)
                }
            }
            if (originName != null) {
                Text(
                    text = originName,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
            if (lineName != null) {
                Text(text = lineName,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}


@Composable
fun StepDeviationCard(
    deviationInfo: DeviationInformation,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = RoyalPurple80
        ),
    ) {
        Column {
            if (deviationInfo.delayInMinutes != 0) {
                Text(
                    text = "Delay: ${deviationInfo.delayInMinutes} minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
            if (!deviationInfo.deviations.isNullOrEmpty()) {
                // Checks if any step has delay or deviations info
                val containsInfo = deviationInfo.deviations.any { item ->
                    item.importanceLevel != 0 || item.text.isNullOrEmpty()
                            || !item.consequence.isNullOrEmpty()
                }
                if(containsInfo) {
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
        .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "${deviation.importanceLevel}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = LightRed
        )
        Text(
            text = "${deviation.text}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "${deviation.consequence}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}