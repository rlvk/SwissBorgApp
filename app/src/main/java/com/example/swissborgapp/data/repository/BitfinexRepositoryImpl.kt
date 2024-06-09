package com.example.swissborgapp.data.repository

import com.example.swissborgapp.data.remote.BitfinexApi
import com.example.swissborgapp.domain.repository.BitfinexRepository
import javax.inject.Inject

class BitfinexRepositoryImpl @Inject constructor(
    private val bitfinexApi: BitfinexApi
): BitfinexRepository {

    override suspend fun getPublicTickers(tickerPairs: String): List<List<Any>>
        = bitfinexApi.getPublicTickers(tickerPairs)
}
