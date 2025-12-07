package com.zincstate.fundmate.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.zincstate.fundmate.data.remote.RetrofitClient
import com.zincstate.fundmate.data.remote.SchemesPagingSource


class AllSchemesViewModel : ViewModel() {

    // The Stream of Paged Data
    // cachedIn(viewModelScope) keeps the data alive during configuration changes (rotation)
    val pagedSchemes = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { SchemesPagingSource(RetrofitClient.api) }
    ).flow.cachedIn(viewModelScope)
}