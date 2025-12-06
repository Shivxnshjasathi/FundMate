package com.zincstate.fundmate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zincstate.fundmate.data.model.SchemeDto
import com.zincstate.fundmate.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define the possible states of the screen
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val schemes: List<SchemeDto>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchSchemes()
    }

    fun fetchSchemes() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Fetching top 50 funds for Phase 1
                val result = RetrofitClient.api.getLatestSchemes(limit = 50)
                _uiState.value = HomeUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to load funds: ${e.message}")
            }
        }
    }
}