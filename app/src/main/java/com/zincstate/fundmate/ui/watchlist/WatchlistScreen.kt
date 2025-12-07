package com.zincstate.fundmate.ui.watchlist

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WatchlistScreen(
    onFundClick: (Int) -> Unit,
    viewModel: WatchlistViewModel = viewModel()
) {
    val watchlist by viewModel.watchlist.collectAsState()

    // BRAND COLORS (Hardcoded for Light Mode consistency)
    val TextDark = Color(0xFF1E2226)

    Scaffold(
        // FORCE BACKGROUND WHITE
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("My Watchlist", color = TextDark) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextDark
                )
            )
        }
    ) { padding ->
        // Animation: Smooth fade between Empty state and List state
        Crossfade(
            targetState = watchlist.isEmpty(),
            label = "watchlist_state",
            modifier = Modifier.padding(padding)
        ) { isEmpty ->
            if (isEmpty) {
                // Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved funds yet",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // List State
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // key is required for proper reordering/deletion animations
                    items(watchlist, key = { it.schemeCode }) { entity ->

                        // Wrapper Box for Item Animation
                        Box(
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
                        ) {
                            // Reuse our existing FundCard by mapping Entity -> DTO
                            val dto = SchemeDto(
                                entity.schemeCode,
                                entity.schemeName,
                                entity.fundHouse,
                                entity.nav,
                                entity.date
                            )

                            // Note: Ensure FundCard also supports/forces light mode colors
                            // inside its definition, or wraps itself in a Surface(color=White).
                            FundCard(scheme = dto, onClick = onFundClick)
                        }
                    }
                }
            }
        }
    }
}