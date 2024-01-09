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

    suspend fun getTravelInformation(travelMode: TravelMode)
    suspend fun getDeviationInformation()
}

class NetworkService : INetworkService {

//    private val _transitSteps = MutableStateFlow<List<Steps>>(emptyList())
//    override val transitSteps: StateFlow<List<Steps>>
//        get() = _transitSteps.asStateFlow()

    private var transitSteps: MutableList<Steps> = mutableListOf()
    private var transitStepsDeviations: MutableList<DeviationInformation> = mutableListOf()

    /**
     *  Makes api call to Google directions and updates the next event info
     */
    override suspend fun getTravelInformation(travelMode: TravelMode) {
        try {
            val lastLocationCoordinates = getLocationOrThrow()

            // Gets the next event happening from the mock calendar
            val nextEvent = getNextCalendarEvent()


            Log.d(TAG, "klockan från calender    ${nextEvent.start}")

            // Sets arrival time for google directions to the event start-time
            val arrivalTime = getArrivalTime(nextEvent.start)

            // Google api-call
            val response = fetchGoogleDirections(
                arrivalTime,
                lastLocationCoordinates,
                nextEvent.location,
                travelMode
            )


            Log.d(TAG, response.routes.first().legs.first().arrivalTime.toString())
            if (travelMode == TravelMode.Transit) {
                processGoogleDirectionsResponse(response)
            } else {
                processNonTransitResponse(response)
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

    private suspend fun processNonTransitResponse(response: GoogleDirectionsResponse) {
        val legs = response.routes.first().legs.first()
        val travelDuration = legs.duration


        val firstEvent = MockEvent.getMockEvents().first()      // TODO: real event

        val firstEventStartLocalTime =
            DateHelpers.convertToSystemTimeZone(firstEvent.start)!!.toEpochSecond()
        val travelTime = travelDuration!!.value!!
        val leaveAtUnixTime = firstEventStartLocalTime.minus(travelTime)
        val leaveAtZonedDateTime = DateHelpers.unixTimeToLocalZonedDateTime(leaveAtUnixTime)
        //val firstEventStartLocalTimeZone = DateHelpers.unixTimeToLocalZonedDateTime(firstEventStartLocalTime)


        // Log.d(TAG, "Next event starts: $firstEventStartLocalTimeZone")
        // Log.d(TAG, "Leave in: " + DateHelpers.formatSecondsToHourMinutes(leaveAtUnixTime - DateHelpers.getLocalTimeInUnixTime()))
        // Log.d(TAG, "Leave at (Local Time): ${DateHelpers.zonedDateTimeToShortFormat(leaveAtZonedDateTime)}")


        val endLocation = legs.endLocation
        val departureTime = DepartureTime(
            text = DateHelpers.zonedDateTimeToShortFormat(leaveAtZonedDateTime),
            timeZone = ZoneId.systemDefault().id,
            value = leaveAtUnixTime.toInt()
        )


        Log.d(TAG, departureTime.toString())


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
        MockTravelInformation.setTravelInformation(
            departureTimeHHMM,
            departureTimeText,
            Pair(endLocation?.lat, endLocation?.lng),
            transitSteps
        )
    }

    private fun calculateDepartureTimeHHMM(departureTime: DepartureTime?): String {
        // Calculate the time to leave (eg: "1h15m")
        val currentTime = ZonedDateTime.now().toEpochSecond()
        return departureTime?.value?.let {
            DateHelpers.formatSecondsToHourMinutes(it - currentTime)
        } ?: ""
    }


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

            MockDeviationInformation.setTransitDeviationInformation(
                transitStepsDeviations = transitStepsDeviations
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching real-time transit data: ${e.message}")
        }
    }

    private suspend fun fetchRealTimeDataForStep(step: Steps): SlRealtimeDataResponse {

        // Fetch real-time data from SL API using the step information

        Log.d(TAG, "google dep. tid : ${transitSteps.first().transitDetails?.departureTime?.text}")

        val stationName =
            step.transitDetails?.departureStop?.name ?: return SlRealtimeDataResponse()

        val siteIdResponse = SlLookUpApi.getSiteIdByStationName(stationName)
        if (!siteIdResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL LookUp api")
        }

        val siteId = siteIdResponse.body()?.responseData?.firstOrNull()?.siteId
            ?: return SlRealtimeDataResponse()

        val realTimeDataResponse = SlRealTimeApi.getRealtimeDataBySiteId(siteId)
        if (!realTimeDataResponse.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to SL RealTime api")
        }

        Log.d(TAG, "sl siteID : ${siteId}")
//        Log.d(TAG, "sl exp. tid : ${realTimeDataResponse.body()?.responseData?.buses?.first()?.expectedDateTime}")
//        Log.d(TAG, "sl time.table. tid : ${realTimeDataResponse.body()?.responseData?.buses?.first()?.timeTabledDateTime}")


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

        /**
         * Ensures that the line number, scheduled departure time, ... from the Google Directions API matches
         * with the line number, ..., ... in the SL Real-Time API.
         */
        when (transportMode) {
            "BUS" -> {
                val busData = realTimeData.responseData?.buses?.find {
                    val slScheduledDepartureTime/*timeTableRealTimeUnix*/ =
                        formatDateTimeStringToUnix(it.timeTabledDateTime) // TODO: namn? Vilket stämmer egentligen?
                    ((((it.lineNumber == lineShortName) && (it.destination == headSign)
                            && (slScheduledDepartureTime /*timeTableRealTimeUnix*/ /*(formatTimeStringToUnix(it.timeTabledDateTime)*/ < scheduledDepartureTime!!))
                            || (slScheduledDepartureTime /*(formatTimeStringToUnix(it.timeTabledDateTime)*/ > scheduledDepartureTime!!)
                            || (slScheduledDepartureTime /*(formatTimeStringToUnix(it.timeTabledDateTime)*/ > scheduledDepartureTime)))
                }
                val test = realTimeData.responseData?.buses?.find {
                    ((((it.lineNumber == lineShortName) && (it.destination == headSign)
                            && ((formatDateTimeStringToUnix(it.timeTabledDateTime) < scheduledDepartureTime!!))
                            || (formatDateTimeStringToUnix(it.timeTabledDateTime) > scheduledDepartureTime!!)
                            || (formatDateTimeStringToUnix(it.timeTabledDateTime) > scheduledDepartureTime))))
                }

//                val test2 = realTimeData.responseData?.buses?.find {
//                    val slCalculatedDepartureTimeDiff =
//                        areTimesSimilar(it.timeTabledDateTime.toString(),
//                            it.expectedDateTime.toString(),
//                        )
//                    ((((it.lineNumber == lineShortName) && (it.destination == headSign)
//                            && ((formatDateTimeStringToUnix(it.timeTabledDateTime) < scheduledDepartureTime!!))
//                            || (formatDateTimeStringToUnix(it.timeTabledDateTime) > scheduledDepartureTime!!)
//                            || (formatDateTimeStringToUnix(it.timeTabledDateTime) > scheduledDepartureTime))))
//                }

                Log.d(
                    TAG,
                    "*|1|* är det samma? ${busData?.timeTabledDateTime}  &  ${test?.timeTabledDateTime}"
                )
                Log.d(
                    TAG,
                    "*|2|* är det samma? ${busData?.expectedDateTime}  &  ${test?.expectedDateTime}"
                )
                Log.d(TAG, "*|3|* är det samma? ${busData?.destination}  &  ${test?.destination}")
                Log.d(
                    TAG,
                    "*|4|* är det samma?!! ${busData?.journeyNumber}  &  ${test?.journeyNumber}"
                )

                realDepartureTime = busData?.expectedDateTime
                deviations = convertDeviations(busData?.deviations)
            }

            "SUBWAY" -> {
                val metroData = realTimeData.responseData?.metros?.find {
                    val slScheduledDepartureTime = formatDateTimeStringToUnix(it.timeTabledDateTime)
                    ((((it.lineNumber == lineShortName) && (it.destination == headSign)
                            && (slScheduledDepartureTime < scheduledDepartureTime!!))
                            || (slScheduledDepartureTime > scheduledDepartureTime!!)
                            || (slScheduledDepartureTime > scheduledDepartureTime)))
                }
                val test2 = realTimeData.responseData?.metros?.find {
                    ((((it.lineNumber == lineShortName) && (it.destination == headSign)
                            && (areTimesSimilar(
                        it.timeTabledDateTime.toString(),
                        formatSecondsToDateTimeString(scheduledDepartureTime)
                    )))))
                }

                Log.d(
                    TAG,
                    "*|G|* är det samma? scheduledDepTime ${
                        formatSecondsToDateTimeString(scheduledDepartureTime)
                    } + MOT ${headSign}"
                )
                Log.d(
                    TAG,
                    "*|1|* är det samma? ${metroData?.timeTabledDateTime}  &  ${test2?.timeTabledDateTime}"
                )
                Log.d(
                    TAG,
                    "*|2|* är det samma? ${metroData?.expectedDateTime}  &  ${test2?.expectedDateTime}"
                )
                Log.d(
                    TAG,
                    "*|3|* är det samma? ${metroData?.destination}  &  ${test2?.destination}"
                )
                Log.d(
                    TAG,
                    "*|4|* är det samma?!! ${metroData?.journeyNumber}  &  ${test2?.journeyNumber}"
                )
//                val metroData =
//                    realTimeData.responseData?.metros?.find {
//                        it.lineNumber == lineShortName && it.destination == headSign
//                    }
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

    fun areTimesSimilar(
        time1: String,
        time2: String
    ): Boolean { // TODO: använda eller ej?? Jämför test. Är Googles och SLs planerade tider lika även fast vi hämtar ny data från google (som då är uppdaterad)?
        Log.d(TAG, "areSimalarTime:  $time1  +  $time2") // TODO: ta bort
        // Example using java.time API (requires API level 26 or using a backport library like ThreeTenABP)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime1 = LocalDateTime.parse(time1, formatter)
        val dateTime2 = LocalDateTime.parse(time2, formatter)

        return Duration.between(dateTime1, dateTime2).abs()
            .toMinutes() <= 1 // for a 1-minute threshold
    }

    private fun convertDeviations(slDeviations: ArrayList<Deviations>?): List<Deviation> {
        return slDeviations?.map {
            Deviation(
                it.text ?: "", it.consequence ?: "", it.importanceLevel ?: 0
            )
        } ?: emptyList()
    }

    private fun calculateDelay(
        scheduledTime: Int?,
        actualTime: String?
    ): Int { //TODO: lik areTimesSimilar(), kan man kombinera användning på något sätt?
        if (scheduledTime == null || actualTime == null) return 0

        // Parse the actual time (expectedDateTime) to a Unix timestamp
        val actualDateTime = formatDateTimeStringToUnix(actualTime)

        // Calculate the delay in seconds
        val delayInSeconds = actualDateTime - scheduledTime
        // Convert the delay to minutes
        return (delayInSeconds / 60).toInt()
    }

    private fun formatSecondsToDateTimeString(seconds: Int?): String { //TODO: flytta till utilities
        val instant = seconds?.let { Instant.ofEpochSecond(it.toLong()) }
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return localDateTime.format(formatter)
    }

    private fun formatDateTimeStringToUnix(timestamp: String?): Long { //TODO: flytta till utilities
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return try {
            LocalDateTime.parse(timestamp, formatter).atZone(ZoneId.systemDefault()).toEpochSecond()
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Error parsing timestamp from Date Time String to Unix: ${e.message}")
            return 0
        }
    }

}