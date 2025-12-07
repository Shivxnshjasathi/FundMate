package com.zincstate.fundmate.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    // Returns a Flow - UI updates automatically when DB changes!
    @Query("SELECT * FROM watchlist")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    // Check if a specific fund is saved (for the Star icon state)
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE schemeCode = :code)")
    fun isWatchlisted(code: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(fund: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE schemeCode = :code")
    suspend fun removeFromWatchlist(code: Int)
}