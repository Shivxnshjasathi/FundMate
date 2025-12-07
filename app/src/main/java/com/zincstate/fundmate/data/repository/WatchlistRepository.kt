package com.zincstate.fundmate.data.repository

import android.content.Context
import androidx.room.Room
import com.zincstate.fundmate.data.local.AppDatabase
import com.zincstate.fundmate.data.local.WatchlistEntity
import kotlinx.coroutines.flow.Flow

// Manual Singleton for dependency injection
object WatchlistRepository {

    private var database: AppDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fundmate_db"
            ).build()
        }
    }

    /**
     * Safe DAO getter.
     * Throws clear error if user forgot to call initialize().
     */
    private fun getDao() = database?.watchlistDao()
        ?: throw IllegalStateException("WatchlistRepository not initialized. Call initialize(context) first.")

    /**
     * IMPORTANT:
     * watchlistFlow MUST be a function, not a property,
     * otherwise it runs BEFORE database is initialized → CRASH.
     */
    fun watchlistFlow(): Flow<List<WatchlistEntity>> =
        getDao().getAllWatchlist()

    /**
     * Returns a Flow<Boolean> from DAO.
     */
    fun isWatchlisted(code: Int): Flow<Boolean> =
        getDao().isWatchlisted(code)

    /**
     * Add or remove from watchlist.
     */
    suspend fun toggleWatchlist(entity: WatchlistEntity, isSaved: Boolean) {
        val dao = getDao()

        if (isSaved) {
            dao.removeFromWatchlist(entity.schemeCode)
        } else {
            dao.addToWatchlist(entity)
        }
    }
}
