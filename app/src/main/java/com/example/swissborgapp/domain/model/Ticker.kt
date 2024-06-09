package com.example.swissborgapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ticker(
    val symbol: String,
    val price: String
): Parcelable {
    fun doesMatchQuery(query: String): Boolean = symbol.contains(query, true)
}

