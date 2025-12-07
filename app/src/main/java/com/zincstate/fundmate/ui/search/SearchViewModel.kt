package com.zincstate.fundmate.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zincstate.fundmate.data.model.SchemeDto
import com.zincstate.fundmate.data.remote.RetrofitClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class) // Needed for debounce/flatMapLatest
class SearchViewModel : ViewModel() {

    // 1. The source of truth for what the user typed
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. The Loading state
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // 3. The magic: Transformations on the query flow
    val searchResults: StateFlow<List<SchemeDto>> = _searchQuery
        .debounce(300L) // Wait 300ms after user stops typing
        .onEach { _isSearching.value = true } // Show loading spinner
        .mapLatest { query ->
            if (query.length < 2) {
                emptyList() // Don't search for single letters
            } else {
                try {
                    // Call the API
                    RetrofitClient.api.searchSchemes(query)
                } catch (e: Exception) {
                    emptyList() // Handle error gracefully (return empty for now)
                }
            }
        }
        .onEach { _isSearching.value = false } // Hide loading spinner
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}
