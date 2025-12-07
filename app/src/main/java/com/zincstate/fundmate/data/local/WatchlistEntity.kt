package com.zincstate.fundmate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val schemeCode: Int,
    val schemeName: String,
    val fundHouse: String,
    val nav: String,       // Storing latest NAV for offline view
    val date: String
)