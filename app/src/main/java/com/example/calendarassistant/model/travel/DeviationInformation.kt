package com.example.calendarassistant.model.travel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 *  Keeps and updates deviation information for transit (SL)
 */

object DeviationInformation {

    private var deviationInformation = MutableSharedFlow<TransitDeviationData>()

    suspend fun setTransitDeviationInformation(
        transitStepsDeviations: List<DeviationData>
    ) {
        deviationInformation.emit(
            TransitDeviationData(
                transitStepsDeviations = transitStepsDeviations
            )
        )
    }

    fun getNextTransitDeviationsInformation():
            SharedFlow<TransitDeviationData> = deviationInformation.asSharedFlow()

}

data class TransitDeviationData (
    val transitStepsDeviations: List<DeviationData>? = emptyList()
)

data class DeviationData(
    val delayInMinutes: Int? = 0,
    val deviations: List<Deviation>? = emptyList()
)

data class Deviation(
    val text: String? = "",
    val consequence: String? = "",
    val importanceLevel: Int? = 0
)
