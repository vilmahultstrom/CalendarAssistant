package com.example.calendarassistant.services

import android.util.Log
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.kth.KthMapper
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.travel.Deviation
import com.example.calendarassistant.model.travel.DeviationData
import com.example.calendarassistant.model.travel.DeviationInformation
import com.example.calendarassistant.model.travel.TravelInformation
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.SlNearbyStopsApi
import com.example.calendarassistant.network.SlRealTimeApi
import com.example.calendarassistant.network.dto.google.directions.GoogleDirectionsResponse
import com.example.calendarassistant.network.dto.google.directions.internal.DepartureTime
import com.example.calendarassistant.network.dto.google.directions.internal.EndLocation
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.dto.sl.realtimeData.SlRealtimeDataResponse
import com.example.calendarassistant.network.dto.sl.realtimeData.internal.Deviations
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.utilities.DateHelpers
import com.example.calendarassistant.utilities.TimeToLeaveDisplay
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val TAG = "NetworkService"

interface INetworkService {
    class NetworkException(message: String) : Exception()

//    val transitSteps: StateFlow<List<Steps>>

    suspend fun getTravelInformation(travelMode: TravelMode, calendarEvent: CalendarEvent?)
    suspend fun getDeviationInformation()
}

class NetworkService : INetworkService {

//    private val _transitSteps = MutableStateFlow<List<Steps>>(emptyList())
//    override val transitSteps: StateFlow<List<Steps>>
//        get() = _transitSteps.asStateFlow()

    private var transitSteps: MutableList<Steps> = mutableListOf()
    private var transitStepsDeviations: MutableList<DeviationData> = mutableListOf()

    /**
     *  Makes api call to Google directions and updates the next event info
     *  Parameters are mode of travel and a calendar event with a non null location
     */
    override suspend fun getTravelInformation(travelMode: TravelMode, calendarEvent: CalendarEvent?) {
        try {
            if (calendarEvent == null) return
            if ((calendarEvent.startDateTime == null) || (calendarEvent.location == null)) return
            val lastLocationCoordinates = getLocationOrThrow()

            // Gets the next event happening from the mock calendar
            //val nextEvent = getNextCalendarEvent()


            // Checks if next location is a Kth lecture hall only for demonstration purposes
            var location = KthMapper.map(calendarEvent.location)
            if (location == null) {
                location = calendarEvent.location
            }


            // Sets arrival time for google directions to the event start-time
            //val arrivalTime = getArrivalTime(nextEvent.start)

            // Google api-call
            val response = fetchGoogleDirections(
                calendarEvent.startDateTime,
                lastLocationCoordinates,
                location,
                travelMode
            )


            if (response.status == null){
                throw INetworkService.NetworkException("Google response was null")
            } else if (response.status == "NOT_FOUND"){
                throw INetworkService.NetworkException("No directions found")
            }



            if (travelMode == TravelMode.Transit) {
                processGoogleDirectionsResponse(response)
            } else {
                processNonTransitResponse(response, calendarEvent.startDateTime)
            }

        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
            Log.e(TAG, "Error in getTravelInformation: ${e.message}")
        }
    }

    private fun getLocationOrThrow(): Pair<String, String> {
        return LocationRepository.getLastLocation()
            ?: throw INetworkService.NetworkException("Location not found")
    }

    private fun getNextCalendarEvent(): MockCalendarEvent {
        return MockEvent.getMockEvents().first()
    }

    private fun getArrivalTime(time: String): Long {
        return ZonedDateTime.parse(time).toEpochSecond() // TODO: vad gör denna, när påverkar den tiden?
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


    /**
     *  Start time is in unixTime
     */
    private suspend fun processNonTransitResponse(response: GoogleDirectionsResponse, startTime: Long) {
        val legs = response.routes.first().legs.first()
        val travelDuration = legs.duration

        val travelTime = travelDuration!!.value!!
        val leaveAtUnixTime = startTime.minus(travelTime)
        val leaveAtZonedDateTime = DateHelpers.unixTimeToLocalZonedDateTime(leaveAtUnixTime)

        val endLocation = legs.endLocation
        val departureTime = DepartureTime(
            text = DateHelpers.zonedDateTimeToShortFormat(leaveAtZonedDateTime),
            timeZone = ZoneId.systemDefault().id,
            value = leaveAtUnixTime.toInt()
        )

        updateTravelInformation(departureTime = departureTime, endLocation = endLocation)
    }

    private suspend fun processGoogleDirectionsResponse(response: GoogleDirectionsResponse) {
        val legs = response.routes.first().legs.first()

        Log.d(TAG, legs.toString())

        transitStepsDeviations.clear()
        transitSteps.clear()
        // Collects steps where transit
        transitSteps.addAll(extractTransitSteps(legs.steps))

        val departureInformation = legs.departureTime
        val endLocation = legs.endLocation

        Log.d(TAG, departureInformation.toString())

        updateTravelInformation(departureInformation, endLocation)
    }

    // Filters and returns a list of steps that involve transit
    private fun extractTransitSteps(steps: List<Steps>): List<Steps> =
        steps.filter { it.travelMode == "TRANSIT" }

    private suspend fun updateTravelInformation(
        departureTime: DepartureTime?,
        endLocation: EndLocation?
    ) {
        val departureTimeHHMM = calculateDepartureTimeHHMM(departureTime)
        val departureTimeText = departureTime?.text

        // Updates set new info, which updates the ui
        TravelInformation.setTravelInformation(
            departureTimeHHMM,
            departureTimeText,
            Pair(endLocation?.lat, endLocation?.lng),
            transitSteps
        )
    }

    private fun calculateDepartureTimeHHMM(departureTime: DepartureTime?): TimeToLeaveDisplay {
        // Calculate the time to leave (eg: "1h15m")
        val currentTime = ZonedDateTime.now().toEpochSecond()
        return DateHelpers.getTimeToLeaveDisplay(
            departureTime?.value?.minus(
                currentTime
            )
        )
    }

// TODO ########################################################################################
    // TODO: Eventuellt dela upp i två klasser?

    /**
     *  Makes api call to SL api and updates the delay and deviation info for the next event
     */
    override suspend fun getDeviationInformation() {
        try {
            transitSteps.map { step ->
                val realTimeData = fetchRealTimeDataForStep(step)
                transitStepsDeviations.add(compareStepWithRealTimeData(step, realTimeData))
            }

            DeviationInformation.setTransitDeviationInformation(
                transitStepsDeviations = transitStepsDeviations
            )

            //TODO: TA BORT/FLYTTA MOCK
            /*
            val mockTransitStepsDeviations = listOf(
                DeviationData(
                    delayInMinutes = 5,
                    deviations = listOf(
                        Deviation(
                            text = "Förseningar pga halt väglag",
                            consequence = "INFORMATION",
                            importanceLevel = 7
                        )
                    )
                ),
                DeviationData(
                    delayInMinutes = 0,
                    deviations = emptyList()
                ),
                DeviationData(
                    delayInMinutes = 10,
                    deviations = listOf(
                        Deviation(
                            text = "Förseningar pga halt väglag",
                            consequence = "INFORMATION",
                            importanceLevel = 7
                        ),
                        Deviation(
                            text = "Kör inte till Sluts hage pga väghinder",
                            consequence = "INFORMATION",
                            importanceLevel = 7
                        )
                    )
                ),
                DeviationData(
                    delayInMinutes = 0,
                    deviations = emptyList()
                )
            )

            DeviationInformation.setTransitDeviationInformation(
                transitStepsDeviations = mockTransitStepsDeviations
            )
            */

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching real-time transit data: ${e.message}")
        }
    }

    private suspend fun fetchRealTimeDataForStep(step: Steps): SlRealtimeDataResponse {
        // Fetch real-time data from SL API using the step information

        val originLat = step.transitDetails?.departureStop?.location?.lat.toString()
        val originLng = step.transitDetails?.departureStop?.location?.lng.toString()

        val siteIdResponse = SlNearbyStopsApi.getSiteIdByCoordinates(originLat, originLng)
        if (!siteIdResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL Nearby Stops api")
        }
        /*
        val stationName = step.transitDetails?.departureStop?.name ?: return SlRealtimeDataResponse()
        val siteIdResponse = SlLookUpApi.getSiteIdByStationName(stationName)
        if (!siteIdResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL LookUp api")
        }*/


        // Extract the 4 last characters from the mainMastExtId
        val siteId = siteIdResponse.body()?.stopLocationOrCoordLocation?.firstOrNull()
            ?.stopLocation?.mainMastExtId?.takeLast(4) ?: return SlRealtimeDataResponse()

        Log.d(TAG, "site id: $siteId")

        val realTimeDataResponse = SlRealTimeApi.getRealtimeDataBySiteId(siteId)
        if (!realTimeDataResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL RealTime api")
        }

        Log.d(TAG, "station name: ${realTimeDataResponse.body()?.responseData?.buses?.find { 
            ((((it.lineNumber == step.transitDetails?.line?.shortName) && 
                    (it.destination == step.transitDetails?.headsign) && 
                    (areTimesSimilar(
                        it.timeTabledDateTime.toString(), DateHelpers.formatSecondsToDateTimeString(
                            step.transitDetails?.departureTime?.value
                        )))
                    )))
        }}")

        return realTimeDataResponse.body() ?: SlRealtimeDataResponse()
    }

    private fun compareStepWithRealTimeData(
        step: Steps,
        realTimeData: SlRealtimeDataResponse
    ): DeviationData {

        val transportMode = step.transitDetails?.line?.vehicle?.type
            ?: return DeviationData(0, emptyList())
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
        transportModeGoogle: String,
        lineShortNameGoogle: String?,
        headSignGoogle: String?,
        scheduledDepartureTimeGoogle: Int?,
        realTimeData: SlRealtimeDataResponse
    ): DeviationData {

        Log.d(TAG, realTimeData.toString())

        val scheduledDepartureTimeSl: String?
        val expectedDepartureTimeSl: String?
        val deviations: List<Deviation>

        /**
         * Ensures that the line number, head sign and scheduled departure time
         * from the Google Directions API matches with the line number,
         * destination and scheduled departure time in the SL Real-Time API.
         */
        when (transportModeGoogle) {
            "BUS" -> {
                val busData = realTimeData.responseData?.buses?.find {
                    ((((it.lineNumber == lineShortNameGoogle) && (it.destination == headSignGoogle)
                            && (areTimesSimilar(it.timeTabledDateTime.toString(),
                        DateHelpers.formatSecondsToDateTimeString(scheduledDepartureTimeGoogle))))))
                }
                scheduledDepartureTimeSl = busData?.timeTabledDateTime
                expectedDepartureTimeSl = busData?.expectedDateTime
                deviations = convertDeviations(busData?.deviations)
            }
            "SUBWAY" -> {
                val metroData = realTimeData.responseData?.metros?.find {
                    ((((it.lineNumber == lineShortNameGoogle) && (it.destination == headSignGoogle)
                            && (areTimesSimilar(it.timeTabledDateTime.toString(),
                        DateHelpers.formatSecondsToDateTimeString(scheduledDepartureTimeGoogle))))))
                }
                scheduledDepartureTimeSl = metroData?.timeTabledDateTime
                expectedDepartureTimeSl = metroData?.expectedDateTime
                deviations = convertDeviations(metroData?.deviations)
            }
            "TRAIN" -> {
                val trainData = realTimeData.responseData?.trains?.find {
                    ((((it.lineNumber == lineShortNameGoogle) && (it.destination == headSignGoogle)
                            && (areTimesSimilar(it.timeTabledDateTime.toString(),
                        DateHelpers.formatSecondsToDateTimeString(scheduledDepartureTimeGoogle))))))
                }
                scheduledDepartureTimeSl = trainData?.timeTabledDateTime
                expectedDepartureTimeSl = trainData?.expectedDateTime
                deviations = convertDeviations(trainData?.deviations)
            }
            "TRAM" -> {
                val tramData = realTimeData.responseData?.trams?.find {
                    ((((it.lineNumber == lineShortNameGoogle) && (it.destination == headSignGoogle)
                            && (areTimesSimilar(it.timeTabledDateTime.toString(),
                        DateHelpers.formatSecondsToDateTimeString(scheduledDepartureTimeGoogle))))))
                }
                scheduledDepartureTimeSl = tramData?.timeTabledDateTime
                expectedDepartureTimeSl = tramData?.expectedDateTime
                deviations = convertDeviations(tramData?.deviations)
            }
            "SHIP" -> {
                val shipData = realTimeData.responseData?.ships?.find {
                    ((((it.lineNumber == lineShortNameGoogle) && (it.destination == headSignGoogle)
                            && (areTimesSimilar(it.timeTabledDateTime.toString(),
                        DateHelpers.formatSecondsToDateTimeString(scheduledDepartureTimeGoogle))))))
                }
                scheduledDepartureTimeSl = shipData?.timeTabledDateTime
                expectedDepartureTimeSl = shipData?.expectedDateTime
                deviations = convertDeviations(shipData?.deviations)
            }
            else -> {
                scheduledDepartureTimeSl = null
                expectedDepartureTimeSl = null
                deviations = emptyList()
            }
        }

        val delayAbsInMinutes = calculateMaxDelay(
            scheduledDepartureTimeGoogle, scheduledDepartureTimeSl, expectedDepartureTimeSl
        )
        val delayInMinutes = calculateDelay(scheduledDepartureTimeGoogle, expectedDepartureTimeSl)
        Log.d(TAG, "DELAY JÄMFÖRELSE: delayAbsInMinutes: $delayAbsInMinutes " +
                " || delayInMinutes: $delayInMinutes")

        return DeviationData(delayInMinutes, deviations)
    }

    fun areTimesSimilar(
        time1: String,
        time2: String
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime1 = LocalDateTime.parse(time1, formatter)
        val dateTime2 = LocalDateTime.parse(time2, formatter)

        return Duration.between(dateTime1, dateTime2).abs()
            .toMinutes() <= 1 // for a 1-minute threshold
    }

    private fun convertDeviations(slDeviations: ArrayList<Deviations>?): List<Deviation> {
        return slDeviations?.map {
            Deviation(
                it.text ?: "",
                it.consequence ?: "",
                it.importanceLevel ?: 0
            )
        } ?: emptyList()
    }

    private fun calculateDelay(scheduledTime: Int?, expectedTime: String?): Int { // TODO: DELETE EN AV DESSA
        if (scheduledTime == null || expectedTime == null) return 0

        // Parse the actual time (expectedDateTime) to a Unix timestamp
        val actualDateTime = DateHelpers.formatDateTimeStringToUnix(expectedTime)

        // Calculate the delay in seconds
        val delayInSeconds = actualDateTime - scheduledTime
        // Convert the delay to minutes
        return (delayInSeconds / 60).toInt()
    }

    private fun calculateMaxDelay( // TODO: DELETE EN AV DESSA
        scheduledTimeGoogle: Int?,
        scheduledTimeSl: String?,
        expectedTimeSl: String?
    ): Int {
        if (expectedTimeSl == null) return 0

        val expectedTimeUnixSl = DateHelpers.formatDateTimeStringToUnix(expectedTimeSl)
        var maxDelayInSeconds = 0L

        scheduledTimeSl?.let {
            val scheduledTimeUnixSl = DateHelpers.formatDateTimeStringToUnix(it)
            maxDelayInSeconds = maxOf(maxDelayInSeconds, expectedTimeUnixSl - scheduledTimeUnixSl)
        }

        scheduledTimeGoogle?.let {
            maxDelayInSeconds = maxOf(maxDelayInSeconds, expectedTimeUnixSl - it)
        }

        // Convert the delay to minutes
        // Only positive values are considered delays
        return if (maxDelayInSeconds > 0) (maxDelayInSeconds / 60).toInt() else 0
    }
}