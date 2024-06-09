package com.example.swissborgapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface BitfinexApi {
    @GET("/v2/tickers?")
    suspend fun getPublicTickers(@Query("symbols") symbols: String): List<List<Any>>
}