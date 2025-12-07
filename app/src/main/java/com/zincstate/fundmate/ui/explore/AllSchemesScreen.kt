package com.zincstate.fundmate.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.zincstate.fundmate.ui.home.FundCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSchemesScreen(
    onFundClick: (Int) -> Unit,
    viewModel: AllSchemesViewModel = viewModel()
) {
    // 1. Collect the Paging Data
    val funds = viewModel.pagedSchemes.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore All Funds") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 2. Render Items
            items(funds.itemCount) { index ->
                val scheme = funds[index]
                if (scheme != null) {
                    FundCard(scheme = scheme, onClick = onFundClick)
                }
            }

            // 3. Handle "Append" Loading State (Spinner at the bottom)
            when (funds.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00D09C))
                        }
                    }
                }
                is LoadState.Error -> {
                    item {
                        Text(
                            text = "Error loading more funds",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {}
            }
        }

        // 4. Handle "Initial" Loading State (Full screen spinner)
        if (funds.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00D09C))
            }
        }
    }
}
