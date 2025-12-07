package com.zincstate.fundmate.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zincstate.fundmate.ui.components.SimpleLineChart
import java.util.Locale
import kotlin.math.pow

// Define colors to match your design
val PrimaryGreen = Color(0xFF00D09C)
val TextDark = Color(0xFF1E2226)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    schemeCode: Int,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    LaunchedEffect(schemeCode) {
        viewModel.fetchFundDetails(schemeCode)
    }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState() // Enable scrolling for the whole screen

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    val isSaved by viewModel.isSaved.collectAsState(initial = false)
                    IconButton(onClick = { viewModel.toggleSave() }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) Color.Red else Color.Gray
                        )
                    }
                }
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
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState) // Make screen scrollable
                    ) {
                        // --- 1. Header ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(
                                    text = state.fundDetails.meta.schemeName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 2,
                                    color = TextDark
                                )
                                Text(
                                    text = state.fundDetails.meta.category,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- 2. Returns & NAV ---
                        Text(
                            text = String.format(Locale.US, "%+.2f%%", state.returnPercentage),
                            color = if(state.returnPercentage >= 0) PrimaryGreen else Color.Red,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.selectedRange.label} Returns",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- 3. Chart ---
                        SimpleLineChart(
                            data = state.chartData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- 4. Time Range Selector ---
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
                                        selectedLabelColor = PrimaryGreen
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if(state.selectedRange == range) Color.Transparent else Color.LightGray,
                                        enabled = true,
                                        selected = state.selectedRange == range
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- 5. SIP Calculator ---
                        Divider(thickness = 0.5.dp, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Pass the current return percentage to the calculator
                        SipCalculator(annualRate = state.returnPercentage)

                        // Bottom padding for scroll
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SipCalculator(annualRate: Double) {
    // State for Calculator
    var isSip by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf(5000f) }
    var durationYears by remember { mutableStateOf(1) } // Default 1 Year

    // Math Logic
    val totalInvestment = if (isSip) amount * 12 * durationYears else amount

    // Logic: If rate is negative, handle gracefully or show loss
    // Using absolute value for calculation demonstration, but mirroring standard SIP logic
    val rate = if (annualRate.isNaN()) 0.0 else annualRate

    val estimatedReturns = calculateReturns(isSip, amount.toDouble(), rate, durationYears)
    val totalValue = totalInvestment + estimatedReturns
    val growthPercentage = if (totalInvestment > 0) (estimatedReturns / totalInvestment) * 100 else 0.0

    Column {
        Text(
            text = "Returns calculator",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle: Monthly SIP vs One-Time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF0F3F6))
                .padding(2.dp)
        ) {
            CalculatorTab(
                text = "Monthly SIP",
                selected = isSip,
                modifier = Modifier.weight(1f),
                onClick = { isSip = true }
            )
            CalculatorTab(
                text = "One-Time",
                selected = !isSip,
                modifier = Modifier.weight(1f),
                onClick = { isSip = false }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Amount Header & Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isSip) "Monthly amount" else "Investment amount",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "₹${amount.toInt()}",
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                fontSize = 16.sp
            )
        }

        Slider(
            value = amount,
            onValueChange = { amount = it },
            valueRange = if (isSip) 500f..50000f else 5000f..500000f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = PrimaryGreen,
                inactiveTrackColor = Color(0xFFE6F9F5)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Duration Chips
        Text("Over the past", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 3, 5).forEach { year ->
                val label = "$year ${if(year > 1) "years" else "year"}"
                FilterChip(
                    selected = durationYears == year,
                    onClick = { durationYears = year },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.White,
                        selectedLabelColor = PrimaryGreen,
                        containerColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (durationYears == year) PrimaryGreen else Color.LightGray,
                        enabled = true,
                        selected = durationYears == year
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Results Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp) // Flat style as per image
        ) {
            Column {
                Text(
                    text = "Total investment of ₹${totalInvestment.toInt()}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Would have become ",
                        color = TextDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹${totalValue.toInt()}",
                        color = TextDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${String.format("%.2f", growthPercentage)}%)",
                        color = PrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) PrimaryGreen else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

// Simple Logic for Calculator approximation
fun calculateReturns(isSip: Boolean, amount: Double, annualRate: Double, years: Int): Double {
    val r = annualRate / 100
    return if (isSip) {
        // Monthly SIP Formula: P * ({[1 + i]^n - 1} / i) * (1 + i) - InvestedAmount
        val i = r / 12
        val n = years * 12.0
        val totalValue = amount * (( (1 + i).pow(n) - 1 ) / i) * (1 + i)
        totalValue - (amount * n)
    } else {
        // Lumpsum Formula: P * (1 + r)^n - P
        val totalValue = amount * (1 + r).pow(years)
        totalValue - amount
    }
}