package com.example.calendarassistant.network

private const val slApiKey = "02e6c4848dae4e39ac256633f954cecc" // todo: lagras som en env.

private interface ISlApi {

}

object SlApi {

    private val slInstance =
        Retrofit.slInstance.create(ISlApi::class.java)

}