package com.example.swissborgapp

import com.example.swissborgapp.domain.model.Ticker
import org.junit.Assert
import org.junit.Test

class TickerTest {

    @Test
    fun verifyThatTickerNameMatchesQuery() {
        // when
        val ticker = Ticker(
            symbol = "tUSD:BTC",
            price = "1"
        )

        // then
        Assert.assertTrue(ticker.doesMatchQuery("usd"))
    }

    @Test
    fun verifyThatTickerNameMatchesQuery_2() {
        // when
        val ticker = Ticker(
            symbol = "tUSD:BTC",
            price = "1"
        )

        // then
        Assert.assertTrue(ticker.doesMatchQuery("USD"))
    }

    @Test
    fun verifyThatTickerNameDoesNotMatchQuery() {
        // when
        val ticker = Ticker(
            symbol = "tUSD:BTC",
            price = "1"
        )

        // then
        Assert.assertFalse(ticker.doesMatchQuery("usdr"))
    }
}