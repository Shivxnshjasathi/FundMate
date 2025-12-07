package com.zincstate.fundmate.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zincstate.fundmate.data.model.SchemeDto


class SchemesPagingSource(
    private val api: MfApi
) : PagingSource<Int, SchemeDto>() {

    override fun getRefreshKey(state: PagingState<Int, SchemeDto>): Int? {
        // If data invalidates, restart from the middle of the current view
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SchemeDto> {
        return try {
            // 1. Get current page index (Default to 0 / offset 0)
            // Ideally, we treat 'key' as the OFFSET directly.
            val currentOffset = params.key ?: 0
            val loadSize = params.loadSize

            // 2. Fetch from Network
            val response = api.getAllSchemes(limit = loadSize, offset = currentOffset)

            // 3. Calculate next key (offset + size)
            // If response is empty, we are at the end (nextKey = null)
            val nextOffset = if (response.isEmpty()) null else currentOffset + loadSize

            LoadResult.Page(
                data = response,
                prevKey = if (currentOffset == 0) null else currentOffset - loadSize,
                nextKey = nextOffset
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}