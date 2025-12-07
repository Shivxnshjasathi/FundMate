package com.zincstate.fundmate.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zincstate.fundmate.data.model.FundDetailResponse
import com.zincstate.fundmate.data.model.NavDataDto
import com.zincstate.fundmate.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Enum for Time Ranges
enum class TimeRange(val label: String, val days: Int) {
    ONE_MONTH("1M", 30),
    SIX_MONTHS("6M", 180),
    ONE_YEAR("1Y", 365),
    THREE_YEARS("3Y", 1095),
    ALL("ALL", Int.MAX_VALUE)
}

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val fundDetails: FundDetailResponse,
        val chartData: List<NavDataDto>, // Filtered data for the chart
        val selectedRange: TimeRange,
        val returnPercentage: Double // Calculated return
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var fullHistory: List<NavDataDto> = emptyList()
    private var cachedResponse: FundDetailResponse? = null

    fun fetchFundDetails(code: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val response = RetrofitClient.api.getSchemeHistory(code)
                cachedResponse = response

                // Parse dates and sort (API usually gives newest first)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                fullHistory = response.data.sortedBy {
                    dateFormat.parse(it.date)?.time ?: 0L
                }

                // Default to 1 Year view
                filterData(TimeRange.ONE_YEAR)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Failed to load details: ${e.message}")
            }
        }
    }

    fun onTimeRangeSelected(range: TimeRange) {
        filterData(range)
    }

    private fun filterData(range: TimeRange) {
        val response = cachedResponse ?: return

        // Simple logic: Take the last N items (approx) or filter by date
        // For simplicity in Phase 3, we'll take items based on days (assuming 1 item per day)
        // A real app would compare Date objects.

        val filteredList = if (range == TimeRange.ALL) {
            fullHistory
        } else {
            fullHistory.takeLast(range.days)
        }

        if (filteredList.isEmpty()) return

        val startNav = filteredList.first().nav.toDouble()
        val endNav = filteredList.last().nav.toDouble()
        val returns = ((endNav - startNav) / startNav) * 100

        _uiState.value = DetailUiState.Success(
            fundDetails = response,
            chartData = filteredList,
            selectedRange = range,
            returnPercentage = returns
        )
    }
}