package com.zincstate.fundmate.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zincstate.fundmate.R
import com.zincstate.fundmate.data.model.SchemeDto
import kotlin.random.Random

// Colors
val PrimaryBlue = Color(0xFF00D09C)
val TextPrimary = Color(0xFF1E2226)
val TextSecondary = Color(0xFF7C8187)
val PositiveGreen = Color(0xFF00C853)
val NegativeRed = Color(0xFFD32F2F)
val SurfaceBackground = Color(0xFFF8F9FB)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onFundClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onExploreClick: () -> Unit,
    onWatchlistClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bullish),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = "Welcome back", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "Investor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                }
                IconButton(onClick = onWatchlistClick) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Alerts", tint = TextPrimary)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceBackground)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryBlue)
                }
                is HomeUiState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Top Movers Section (Gainers/Losers Grid)
                        item {
                            // Pass first 8 items to simulate gainers/losers
                            val sampleList = state.schemes.take(8)
                            if (sampleList.isNotEmpty()) {
                                TopMoversSection(schemes = sampleList, onFundClick = onFundClick)
                            }
                        }

                        // 2. Section Header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Explore Funds", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        // 3. Main List
                        items(state.schemes) { scheme ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                FundCard(scheme, onFundClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- NEW COMPONENT: Top Movers (Gainers/Losers) ---
@Composable
fun TopMoversSection(schemes: List<SchemeDto>, onFundClick: (Int) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Gainers, 1 = Losers

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        // Toggle Tabs
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoverTab(text = "Top Gainers", isSelected = selectedTab == 0) { selectedTab = 0 }
            Spacer(modifier = Modifier.width(12.dp))
            MoverTab(text = "Top Losers", isSelected = selectedTab == 1) { selectedTab = 1 }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2x2 Grid Layout
        // We take 4 items based on the tab.
        // In a real app, you'd filter by positive/negative returns.
        // Here we just slice the list for demo.
        val displayItems = if (selectedTab == 0) schemes.take(4) else schemes.drop(4).take(4)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Row 1 (Items 0 and 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (displayItems.isNotEmpty()) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniFundCard(displayItems[0], isGainer = selectedTab == 0, onClick = onFundClick)
                    }
                }
                if (displayItems.size > 1) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniFundCard(displayItems[1], isGainer = selectedTab == 0, onClick = onFundClick)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2 (Items 2 and 3)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (displayItems.size > 2) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniFundCard(displayItems[2], isGainer = selectedTab == 0, onClick = onFundClick)
                    }
                }
                if (displayItems.size > 3) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniFundCard(displayItems[3], isGainer = selectedTab == 0, onClick = onFundClick)
                    }
                }
            }
        }
    }
}

@Composable
fun MoverTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else Color.LightGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) PrimaryBlue else TextSecondary
        )
    }
}

@Composable
fun MiniFundCard(scheme: SchemeDto, isGainer: Boolean, onClick: (Int) -> Unit) {
    // Mock Random Return
    val returnVal = remember { Random.nextDouble(1.0, 10.0) }
    val sign = if (isGainer) "+" else "-"
    val color = if (isGainer) PositiveGreen else NegativeRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Fixed height for grid uniformity
            .clickable { onClick(scheme.schemeCode) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Placeholder & Name
            Row(verticalAlignment = Alignment.Top) {

                Text(
                    text = scheme.schemeName ?: "Fund",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }

            // Price & Returns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val rawNav = scheme.nav
                val price = if (!rawNav.isNullOrEmpty() && rawNav != "null") "₹${rawNav}" else "N/A"

                Text(text = price, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

                Text(
                    text = "$sign${String.format("%.2f", returnVal)}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

// --- EXISTING FUND CARD ---
@Composable
fun FundCard(scheme: SchemeDto, onClick: (Int) -> Unit) {
    // --- MOCK DATA ---
    val mockReturn = remember { Random.nextDouble(-5.0, 15.0) }
    // -----------------

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(scheme.schemeCode) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scheme.schemeName ?: "Unknown Fund",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scheme.fundHouse ?: "Mutual Fund",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val rawNav = scheme.nav
                val isValidNav = !rawNav.isNullOrEmpty() && rawNav != "null"

                if (isValidNav) {
                    Text(
                        text = "₹$rawNav",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val isPositive = mockReturn >= 0
                    val sign = if (isPositive) "+" else ""
                    Text(
                        text = "$sign${String.format("%.2f", mockReturn)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPositive) PositiveGreen else NegativeRed
                    )
                } else {
                    Text(text = "N/A", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }
    }
}