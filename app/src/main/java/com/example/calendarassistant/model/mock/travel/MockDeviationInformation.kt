package com.example.calendarassistant.model.mock.travel

import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow


object MockDeviationInformation {

    private val _transitDeviations = MutableStateFlow<List<DeviationInformation>>(listOf())
    var transitDeviations: StateFlow<List<DeviationInformation>> = _transitDeviations

    private var deviationInformation = MutableSharedFlow<TransitDeviationInformation>()

    suspend fun setTransitDeviationInformation(
        transitStepsDeviations: List<DeviationInformation>
    ) {
        deviationInformation.emit(
            TransitDeviationInformation(
                transitStepsDeviations = transitStepsDeviations
            )
        )

        //_transitDeviations.value = transitStepsDeviations // TODO: vilket sätt borde man göra?
    }

    fun getNextTransitDeviationsInformation():
            SharedFlow<TransitDeviationInformation> = deviationInformation.asSharedFlow()

}

data class TransitDeviationInformation (
    val transitStepsDeviations: List<DeviationInformation>? = emptyList()
)

data class DeviationInformation(
    val delayInMinutes: Int? = 0,
    val deviations: List<Deviation>? = emptyList()
)

data class Deviation(
    val text: String? = "",
    val consequence: String? = "",
    val importanceLevel: Int? = 0
)
