package com.example.calendarassistant.services

import android.util.Log
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.mock.travel.Deviation
import com.example.calendarassistant.model.mock.travel.DeviationInformation
import com.example.calendarassistant.model.mock.travel.MockDeviationInformation
import com.example.calendarassistant.model.mock.travel.MockTravelInformation
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.SlLookUpApi
import com.example.calendarassistant.network.SlRealTimeApi
import com.example.calendarassistant.network.dto.google.directions.GoogleDirectionsResponse
import com.example.calendarassistant.network.dto.google.directions.internal.DepartureTime
import com.example.calendarassistant.network.dto.google.directions.internal.EndLocation
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.dto.sl.realtimeData.SlRealtimeDataResponse
import com.example.calendarassistant.network.dto.sl.realtimeData.internal.Deviations
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.utilities.DateHelpers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val TAG = "NetworkService"

class NetworkService : INetworkService {
/*

    private var transitSteps: MutableList<Steps> = mutableListOf()

    */
/**
     *  Makes api call to Google directions and updates the next event info
     *//*

    override suspend fun getTravelInformation(travelMode: TravelMode) {
        try {
            val lastLocationCoordinates = LocationRepository.getLastLocation()
                ?: throw INetworkService.NetworkException("Location not found")

            // Gets the next event happening from the mock
            val nextEvent = MockEvent.getMockEvents().first()

            // Sets arrival time for google directions to the event start-time
            val arrivalTime = ZonedDateTime.parse(nextEvent.start).toEpochSecond()

            // Google api-call
            val response = fetchGoogleDirections(
                arrivalTime,
                lastLocationCoordinates,
                nextEvent.location,
                travelMode
            )

            val legs = response.routes.first().legs.first()

            Log.d(TAG, legs.toString())

            // Collects steps where transit
            // TODO: use this list to make api calls for traffic events (delays)
            transitSteps.clear()
            transitSteps.addAll(extractTransitSteps(legs.steps))

            // Contains time of departure in text and unix time
            val departureInformation = legs.departureTime

            // Contains coordinates for end location (used for opening google maps from ui)
            val endLocation = legs.endLocation

            if (departureInformation != null) {
                val departureTimeHHMM = calculateDepartureTimeHHMM(departureInformation)
                val departureTimeText = departureInformation.text

                updateMockTravelInformation(
                    departureTimeHHMM,
                    departureTimeText,
                    endLocation,
                    transitSteps
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }

    private suspend fun fetchGoogleDirections(
        arrivalTime: Long,
        origin: Pair<String, String>,
        destination: String,
        mode: TravelMode
    ): GoogleDirectionsResponse {
        val response = GoogleApi.getDirectionsByCoordinatesOriginDestinationPlace(
            arrivalTime = arrivalTime,
            origin = origin,
            destination = destination,
            mode = mode
        )
        if (!response.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to Google Maps api")
        }
        return response.body()!!
    }

    // Filters and returns a list of steps that involve transit.
    private fun extractTransitSteps(steps: List<Steps>): List<Steps> =
        steps.filter { it.travelMode == "TRANSIT" }

    private fun calculateDepartureTimeHHMM(departureTime: DepartureTime?): String {
        // Calculate the time to leave (eg: "1h15m")
        val currentTime = ZonedDateTime.now().toEpochSecond()
        return departureTime?.value?.let {
            DateHelpers.formatSecondsToHourMinutes(it - currentTime)
        } ?: ""
    }

    private suspend fun updateMockTravelInformation(
        departureTimeHHMM: String,
        departureTimeText: String?,
        endLocation: EndLocation?,
        transitSteps: List<Steps>
    ) {
        // Updates set new info, which updates the ui
        MockTravelInformation.setTravelInformation(
            departureTimeHHMM,
            departureTimeText,
            Pair(endLocation?.lat, endLocation?.lng),
            transitSteps = transitSteps
        )
    }


    */
/**
     *  Makes api call to SL api and updates the delay and deviation info in for the next event
     *//*

    override suspend fun getDeviationInformation() {
        try {
            transitSteps.map { step ->
                val realTimeData = fetchRealTimeDataForStep(step)
                compareStepWithRealTimeData(step, realTimeData)
            }.toMutableList()

            // TODO: implement information update for ui

        } catch (e: Exception) {
            Log.d(TAG, "Error fetching real-time transit data: ${e.message}")
        }
    }

    private suspend fun fetchRealTimeDataForStep(step: Steps): SlRealtimeDataResponse {
        // Fetch real-time data from SL API using the step information
        val stationName = step.transitDetails?.departureStop?.name ?: return SlRealtimeDataResponse()
        val siteIdResponse = SlApi.getSiteIdByStationName(stationName = stationName)
        val siteId = siteIdResponse.body()?.responseData?.firstOrNull()?.siteId ?: return SlRealtimeDataResponse()
        return SlApi.getRealtimeDataBySiteId(siteId = siteId).body() ?: SlRealtimeDataResponse()
    }

    private fun compareStepWithRealTimeData(
        step: Steps,
        realTimeData: SlRealtimeDataResponse
    ): DeviationInformation {
        val transportMode = step.transitDetails?.line?.vehicle?.type
            ?: return DeviationInformation(0, null)
        val lineShortName = step.transitDetails?.line?.shortName
        val scheduledDepartureTime = step.transitDetails?.departureTime?.value

        var realDepartureTime: String? = null
        var deviations: List<Deviation>? = null

        when (transportMode) {
            "BUS" -> {
                val busData = realTimeData.responseData?.buses?.find { it.lineNumber == lineShortName }
                realDepartureTime = busData?.expectedDateTime
                deviations = convertDeviations(busData?.deviations)
            }
            "SUBWAY" -> {
                val metroData = realTimeData.responseData?.metros?.find { it.lineNumber == lineShortName }
                realDepartureTime = metroData?.expectedDateTime
                deviations = convertDeviations(metroData?.deviations)
            }
            "TRAIN" -> {
                val trainData = realTimeData.responseData?.trains?.find { it.lineNumber == lineShortName }
                realDepartureTime = trainData?.expectedDateTime
                deviations = convertDeviations(trainData?.deviations)
            }
            "TRAM" -> {
                val tramData = realTimeData.responseData?.trams?.find { it.lineNumber == lineShortName }
                realDepartureTime = tramData?.expectedDateTime
                deviations = convertDeviations(tramData?.deviations)
            }
            "SHIP" -> {
                val shipData = realTimeData.responseData?.ships?.find { it.lineNumber == lineShortName }
                realDepartureTime = shipData?.expectedDateTime
                deviations = convertDeviations(shipData?.deviations)
            }
            else -> {
                realDepartureTime = null
                deviations = emptyList()
            }
        }

        val delayInMinutes = calculateDelay(scheduledDepartureTime, realDepartureTime)
        return DeviationInformation(delayInMinutes, deviations)
    }

    private fun convertDeviations(slDeviations: ArrayList<Deviations>?): List<Deviation> {
        return slDeviations?.map {
            Deviation(
                it.text ?: "",it.consequence ?: "",it.importanceLevel ?: 0
            )
        } ?: emptyList()
    }

    private fun calculateDelay(scheduledTime: Int?, actualTime: String?): Int {
        if (scheduledTime == null || actualTime == null) return 0

        // Parse the actual time (expectedDateTime) to a Unix timestamp
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val actualDateTime = try {
            LocalDateTime.parse(actualTime, formatter).atZone(ZoneId.systemDefault()).toEpochSecond()
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            return 0
        }

        // Calculate the delay in seconds
        val delayInSeconds = actualDateTime - scheduledTime

        // Convert the delay to minutes
        return (delayInSeconds / 60).toInt()
    }
*/

/*

    override suspend fun getTravelInformationn(travelMode: TravelMode) {
        try {
            when (travelMode) {
                TravelMode.Transit -> getTransitTravelInformation()
                TravelMode.Driving -> getDrivingTravelInformation()
                TravelMode.Bicycling -> getBicyclingTravelInformation()
                TravelMode.Walking -> getWalkingTravelInformation()
                else -> null
            }
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }

    private suspend fun getTransitTravelInformation() {
        TODO("Not yet implemented")
    }

    private fun getDrivingTravelInformation() {
        TODO("Not yet implemented")
    }

    private fun getBicyclingTravelInformation() {
        TODO("Not yet implemented")
    }

    private fun getWalkingTravelInformation() {
        TODO("Not yet implemented")
    }

*/

    fun hello(): String {
        return "Hello"
    }




    private var transitSteps: MutableList<Steps> = mutableListOf()

    private var transitStepsDeviations: MutableList<DeviationInformation> = mutableListOf()

    /**
     *  Makes api call to Google directions and updates the next event info
     */
    override suspend fun getTravelInformation(travelMode: TravelMode) {
        try {
//            val lastLocationCoordinates = Pair("59.280183400", "18.0578712")
            val lastLocationCoordinates = getLocationOrThrow()
            val nextEvent = getNextEvent()
            val arrivalTime = getArrivalTime(nextEvent.start)
            val response = fetchGoogleDirections(arrivalTime, lastLocationCoordinates, nextEvent.location, travelMode)
            processGoogleDirectionsResponse(response)
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
            Log.e(TAG, "Error in getTravelInformation: ${e.message}")
        }
    }

    private fun getLocationOrThrow(): Pair<String, String> {
        return LocationRepository.getLastLocation()
            ?: throw INetworkService.NetworkException("Location not found")
    }

    private fun getNextEvent(): MockCalendarEvent {
        return MockEvent.getMockEvents().first()
    }

    private fun getArrivalTime(time: String): Long {
        return ZonedDateTime.parse(time).toEpochSecond()
    }

    private suspend fun fetchGoogleDirections(
        arrivalTime: Long,
        origin: Pair<String, String>,
        destination: String,
        mode: TravelMode
    ): GoogleDirectionsResponse {
        val response = GoogleApi.getDirectionsByCoordinatesOriginDestinationPlace(
            arrivalTime = arrivalTime,
            origin = origin,
            destination = destination,
            mode = mode
        )
        if (!response.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to Google Maps api")
        }
        return response.body()!!
    }

    private suspend fun processGoogleDirectionsResponse(response: GoogleDirectionsResponse) {
        val legs = response.routes.first().legs.first()

        Log.d(TAG, legs.toString())

        transitStepsDeviations.clear()
        transitSteps.clear()
        transitSteps.addAll(extractTransitSteps(legs.steps))

        val departureInformation = legs.departureTime
        val endLocation = legs.endLocation
        updateTravelInformation(departureInformation, endLocation)
    }

    private fun extractTransitSteps(steps: List<Steps>): List<Steps> =
        steps.filter { it.travelMode == "TRANSIT" }

    private suspend fun updateTravelInformation(departureTime: DepartureTime?, endLocation: EndLocation?) {
        val departureTimeHHMM = calculateDepartureTimeHHMM(departureTime)
        val departureTimeText = departureTime?.text

        MockTravelInformation.setTravelInformation(
            departureTimeHHMM,
            departureTimeText,
            Pair(endLocation?.lat, endLocation?.lng),
            transitSteps
        )
    }

    private fun calculateDepartureTimeHHMM(departureTime: DepartureTime?): String {
        val currentTime = ZonedDateTime.now().toEpochSecond()
        return departureTime?.value?.let {
            DateHelpers.formatSecondsToHourMinutes(it - currentTime)
        } ?: ""
    }



    /**
     *  Makes api call to SL api and updates the delay and deviation info for the next event
     */
    override suspend fun getDeviationInformation() {
        try {
            transitSteps.map { step ->
                val realTimeData = fetchRealTimeDataForStep(step)
                transitStepsDeviations.add(compareStepWithRealTimeData(step, realTimeData))
            }

            MockDeviationInformation.setTransitDeviationInformation(
                transitStepsDeviations = transitStepsDeviations
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching real-time transit data: ${e.message}")
        }
    }

    private suspend fun fetchRealTimeDataForStep(step: Steps): SlRealtimeDataResponse {
        Log.d(TAG, "--- FetchRealTimeDataForStep(steps) ---")

        val stationName = step.transitDetails?.departureStop?.name ?: return SlRealtimeDataResponse()
        Log.d(TAG, "--- stationName = $stationName ---")

        val siteIdResponse = SlLookUpApi.getSiteIdByStationName(stationName)
        Log.d(TAG, "--- siteIdResponse = $siteIdResponse. ---")

        if (!siteIdResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL LookUp api")
        }

        val siteId = siteIdResponse.body()?.responseData?.firstOrNull()?.siteId
            ?: return SlRealtimeDataResponse()
        Log.d(TAG, "--- siteId = $siteId ---")

        val realTimeDataResponse = SlRealTimeApi.getRealtimeDataBySiteId(siteId)
        Log.d(TAG, "--- RT data response = $realTimeDataResponse. ---")

        if (!realTimeDataResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL RealTime api")
        }

        return realTimeDataResponse.body() ?: SlRealtimeDataResponse()
    }

    private fun compareStepWithRealTimeData(
        step: Steps,
        realTimeData: SlRealtimeDataResponse
    ): DeviationInformation {

        val transportMode = step.transitDetails?.line?.vehicle?.type
            ?: return DeviationInformation(0, emptyList())
        val lineShortName = step.transitDetails?.line?.shortName
        val headSign = step.transitDetails?.headsign
        val scheduledDepartureTime = step.transitDetails?.departureTime?.value

        return findRealTimeData(
            transportMode,
            lineShortName,
            headSign,
            scheduledDepartureTime,
            realTimeData
        )
    }

    private fun findRealTimeData(
        transportMode: String,
        lineShortName: String?,
        headSign: String?,
        scheduledDepartureTime: Int?,
        realTimeData: SlRealtimeDataResponse
    ): DeviationInformation {

        val realDepartureTime: String?
        val deviations: List<Deviation>

        when (transportMode) {
            "BUS" -> {
                val busData =
                    realTimeData.responseData?.buses?.find {
                        it.lineNumber == lineShortName && it.destination == headSign
                    }
                realDepartureTime = busData?.expectedDateTime
                deviations = convertDeviations(busData?.deviations)
            }
            "SUBWAY" -> {
                val metroData =
                    realTimeData.responseData?.metros?.find {
                        it.lineNumber == lineShortName && it.destination == headSign
                    }
                realDepartureTime = metroData?.expectedDateTime
                deviations = convertDeviations(metroData?.deviations)
            }
            "TRAIN" -> {
                val trainData =
                    realTimeData.responseData?.trains?.find {
                        it.lineNumber == lineShortName && it.destination == headSign
                    }
                realDepartureTime = trainData?.expectedDateTime
                deviations = convertDeviations(trainData?.deviations)
            }
            "TRAM" -> {
                val tramData =
                    realTimeData.responseData?.trams?.find {
                        it.lineNumber == lineShortName && it.destination == headSign
                    }
                realDepartureTime = tramData?.expectedDateTime
                deviations = convertDeviations(tramData?.deviations)
            }
            "SHIP" -> {
                val shipData =
                    realTimeData.responseData?.ships?.find {
                        it.lineNumber == lineShortName && it.destination == headSign
                    }
                realDepartureTime = shipData?.expectedDateTime
                deviations = convertDeviations(shipData?.deviations)
            }
            else -> {
                realDepartureTime = null
                deviations = emptyList()
            }
        }

        val delayInMinutes = calculateDelay(scheduledDepartureTime, realDepartureTime)
        return DeviationInformation(delayInMinutes, deviations)
    }

    private fun convertDeviations(slDeviations: ArrayList<Deviations>?): List<Deviation> {
        return slDeviations?.map {
            Deviation(
                it.text ?: "",it.consequence ?: "",it.importanceLevel ?: 0
            )
        } ?: emptyList()
    }

    private fun calculateDelay(scheduledTime: Int?, actualTime: String?): Int {
        if (scheduledTime == null || actualTime == null) return 0

        // Parse the actual time (expectedDateTime) to a Unix timestamp
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val actualDateTime = try {
            LocalDateTime.parse(actualTime, formatter).atZone(ZoneId.systemDefault()).toEpochSecond()
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Error parsing actualTime: ${e.message}")
            return 0
        }

        // Calculate the delay in seconds
        val delayInSeconds = actualDateTime - scheduledTime
        // Convert the delay to minutes
        return (delayInSeconds / 60).toInt()
    }

}

interface INetworkService {
    class NetworkException(message: String) : Exception()

    suspend fun getTravelInformation(travelMode: TravelMode)

    suspend fun getDeviationInformation()
}