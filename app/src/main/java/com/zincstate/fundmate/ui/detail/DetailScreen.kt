package com.zincstate.fundmate.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zincstate.fundmate.ui.components.SimpleLineChart
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    schemeCode: Int,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    // Trigger fetch when screen opens
    LaunchedEffect(schemeCode) {
        viewModel.fetchFundDetails(schemeCode)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Error -> {
                    Text("Error: ${state.message}", modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Success -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 1. Header (Logo & Name)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFF0F3F6), CircleShape)
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = state.fundDetails.meta.schemeName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 2
                                )
                                Text(
                                    text = state.fundDetails.meta.category,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Returns & Current NAV
                        Text(
                            text = String.format(Locale.US, "%+.2f%%", state.returnPercentage),
                            color = if(state.returnPercentage >= 0) Color(0xFF00D09C) else Color.Red,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.selectedRange.label} Returns",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // 3. Chart
                        SimpleLineChart(
                            data = state.chartData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. Time Range Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TimeRange.values().forEach { range ->
                                FilterChip(
                                    selected = state.selectedRange == range,
                                    onClick = { viewModel.onTimeRangeSelected(range) },
                                    label = { Text(range.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE6F9F5),
                                        selectedLabelColor = Color(0xFF00D09C)
                                    ),
//                                    border = FilterChipDefaults.filterChipBorder(
//                                        borderColor = Color.Transparent,
//                                        selectedBorderColor = Color.Transparent
//                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
