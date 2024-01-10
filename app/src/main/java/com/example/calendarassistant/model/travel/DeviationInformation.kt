package com.example.calendarassistant.model.travel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow


object DeviationInformation {

    private val _transitDeviations = MutableStateFlow<List<DeviationData>>(listOf())
    var transitDeviations: StateFlow<List<DeviationData>> = _transitDeviations

    private var deviationInformation = MutableSharedFlow<TransitDeviationData>()

    suspend fun setTransitDeviationInformation(
        transitStepsDeviations: List<DeviationData>
    ) {
        deviationInformation.emit(
            TransitDeviationData(
                transitStepsDeviations = transitStepsDeviations
            )
        )

        // TODO: på vilket sätt borde man göra?
        //_transitDeviations.value = transitStepsDeviations
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
