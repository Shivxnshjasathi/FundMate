package com.zincstate.fundmate.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zincstate.fundmate.data.model.FundDetailResponse
import com.zincstate.fundmate.data.model.NavDataDto
import com.zincstate.fundmate.data.remote.RetrofitClient
import com.zincstate.fundmate.data.repository.WatchlistRepository
import com.zincstate.fundmate.data.local.WatchlistEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Time ranges
enum class TimeRange(val label: String, val days: Int) {
    ONE_MONTH("1M", 30),
    SIX_MONTHS("6M", 180),
    ONE_YEAR("1Y", 365),
    THREE_YEARS("3Y", 1095),
    ALL("ALL", Int.MAX_VALUE)
}

// UI State
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val fundDetails: FundDetailResponse,
        val chartData: List<NavDataDto>,
        val selectedRange: TimeRange,
        val returnPercentage: Double
    ) : DetailUiState

    data class Error(val message: String) : DetailUiState
}

class DetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // -------------------------------
    // WATCHLIST STATE
    // -------------------------------
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private var currentCode: Int? = null

    private var fullHistory: List<NavDataDto> = emptyList()
    private var cachedResponse: FundDetailResponse? = null


    fun fetchFundDetails(code: Int) {
        currentCode = code

        // Observe DB Watchlist State
        viewModelScope.launch {
            WatchlistRepository.isWatchlisted(code)
                .collect { saved ->
                    _isSaved.value = saved
                }
        }

        // Fetch fund details & history
        viewModelScope.launch {
            try {
                _uiState.value = DetailUiState.Loading

                val response = RetrofitClient.api.getSchemeHistory(code)
                cachedResponse = response

                val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                fullHistory = response.data.sortedBy {
                    df.parse(it.date)?.time ?: 0L
                }

                // Default 1Y view
                filterData(TimeRange.ONE_YEAR)

            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Failed: ${e.message}")
            }
        }
    }

    // -------------------------------
    // TOGGLE SAVE (WATCHLIST ADD/REMOVE)
    // -------------------------------
    fun toggleSave() {
        val code = currentCode ?: return

        viewModelScope.launch {

            val response = cachedResponse ?: return@launch
            val meta = response.meta
            val latest = fullHistory.lastOrNull()

            val entity = WatchlistEntity(
                schemeCode = meta.schemeCode,
                schemeName = meta.schemeName,
                fundHouse = meta.fundHouse ?: "",
                nav = latest?.nav ?: "0.0",
                date = latest?.date ?: ""
            )

            WatchlistRepository.toggleWatchlist(
                entity = entity,
                isSaved = isSaved.value
            )
        }
    }

    // -------------------------------
    // TIME RANGE LOGIC
    // -------------------------------
    fun onTimeRangeSelected(range: TimeRange) {
        filterData(range)
    }

    private fun filterData(range: TimeRange) {
        val response = cachedResponse ?: return

        val filtered = if (range == TimeRange.ALL) {
            fullHistory
        } else {
            fullHistory.takeLast(range.days)
        }

        if (filtered.isEmpty()) return

        val start = filtered.first().nav.toDouble()
        val end = filtered.last().nav.toDouble()
        val returns = ((end - start) / start) * 100

        _uiState.value = DetailUiState.Success(
            fundDetails = response,
            chartData = filtered,
            selectedRange = range,
            returnPercentage = returns
        )
    }
}
