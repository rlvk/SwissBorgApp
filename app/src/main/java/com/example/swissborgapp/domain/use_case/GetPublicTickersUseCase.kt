package com.example.swissborgapp.domain.use_case

import com.example.swissborgapp.domain.model.Ticker
import com.example.swissborgapp.domain.repository.BitfinexRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPublicTickersUseCase @Inject constructor(
    private val bitfinexRepository: BitfinexRepository
) {

    operator fun invoke(): Flow<Result<List<Ticker>>> = timer.map {
        try {
            val tickers = bitfinexRepository.getPublicTickers(tickerPairs = TICKERS_PAIRS).map {
                Ticker(
                    symbol = it[0].toString(),
                    price = it[7].toString()
                )
            }
            Result.success(
                value = tickers
            )
        } catch (e: HttpException) {
            Result.failure(
                Throwable(
                    message = e.localizedMessage ?: "An unexpected error occured"
                )
            )
        } catch (e: IOException) {
            Result.failure(
                Throwable(
                    message = e.localizedMessage ?: "Could not reach the server. Check your internet connection"
                )
            )
        }
    }

    private val timer = flow {
        while (currentCoroutineContext().isActive) {
            emit(Unit)
            delay(5_000)
        }
    }

    companion object {
        private const val TICKERS_PAIRS = "tBTCUSD,tETHUSD,tCHSB:USD,tLTCUSD,tXRPUSD,tDSHUSD,tRRTUSD,t EOSUSD,tSANUSD,tDATUSD,tSNTUSD,tDOGE:USD,tLUNA:USD,tMATIC:USD,tNEXO :USD,tOCEAN:USD,tBEST:USD,tAAVE:USD,tPLUUSD,tFILUSD"
    }
}