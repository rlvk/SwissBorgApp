package com.example.swissborgapp.domain.repository

interface BitfinexRepository {
    suspend fun getPublicTickers(tickerPairs: String): List<List<Any>>
}