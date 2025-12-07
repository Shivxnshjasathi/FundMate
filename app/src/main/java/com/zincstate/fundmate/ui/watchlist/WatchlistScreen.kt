package com.zincstate.fundmate.ui.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zincstate.fundmate.data.model.SchemeDto
import com.zincstate.fundmate.ui.home.FundCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    onFundClick: (Int) -> Unit,
    viewModel: WatchlistViewModel = viewModel()
) {
    val watchlist by viewModel.watchlist.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Watchlist") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (watchlist.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No saved funds yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(watchlist) { entity ->
                    // Reuse our existing FundCard by mapping Entity -> DTO
                    val dto = SchemeDto(
                        entity.schemeCode,
                        entity.schemeName,
                        entity.fundHouse,
                        entity.nav,
                        entity.date
                    )
                    FundCard(scheme = dto, onClick = onFundClick)
                }
            }
        }
    }
}
