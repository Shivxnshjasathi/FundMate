package com.zincstate.fundmate.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zincstate.fundmate.data.local.WatchlistEntity
import com.zincstate.fundmate.data.repository.WatchlistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel : ViewModel() {

    // Correct: watchlistFlow is a function
    val watchlist: StateFlow<List<WatchlistEntity>> =
        WatchlistRepository.watchlistFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun removeFromWatchlist(code: Int) {
        viewModelScope.launch {
            WatchlistRepository.toggleWatchlist(
                entity = WatchlistEntity(
                    schemeCode = code,
                    schemeName = "",
                    fundHouse = "",
                    nav = "",
                    date = ""
                ),
                isSaved = true
            )
        }
    }
}
