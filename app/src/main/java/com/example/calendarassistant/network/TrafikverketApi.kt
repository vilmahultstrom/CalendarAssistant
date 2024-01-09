package com.example.calendarassistant.network

import retrofit2.Response
import retrofit2.http.POST

private const val trafikverketApiKey = "93b5f7e313e94be39e592be7341ec6d2"

private interface ITrafikverketApi {
    @POST("/v2/data.json")
    suspend fun dummyMethod(

    ) : Response<String> {
        return Response.success(200, "hello")
    }

}

object TrafikverketApi {

    private val trafikverketInstance =
        Retrofit.trafikverketInstance.create(ITrafikverketApi::class.java)


    // Implement methods here

    suspend fun dummyMethod() : Response<String> {
        return trafikverketInstance.dummyMethod()
    }


}