package com.zincstate.fundmate.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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

    // State for the selected category filter
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        containerColor = Color.White,
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
                    painter = painterResource(id = R.drawable.bullish), // Ensure you have this drawable
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

                    // --- FILTER LOGIC (Simulated for Demo) ---
                    // In a real app, your API would return the category type.
                    // Here we filter based on hashcode or name just to show the list changing.
                    val filteredList = remember(selectedCategory, state.schemes) {
                        if (selectedCategory == "All") {
                            state.schemes
                        } else {
                            // SIMULATION: Filtering arbitrarily to show UI change
                            state.schemes.filterIndexed { index, scheme ->
                                when (selectedCategory) {
                                    "Large Cap" -> index % 3 == 0 // Show every 3rd item
                                    "Mid Cap" -> index % 3 == 1   // Show different items
                                    "Small Cap" -> index % 3 == 2
                                    else -> true
                                }
                            }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.animateContentSize() // Smooth animation when list changes
                    ) {
                        // 1. Top Movers Section (Always Visible)
                        item {
                            val sampleList = state.schemes.take(8)
                            if (sampleList.isNotEmpty()) {
                                TopMoversSection(schemes = sampleList, onFundClick = onFundClick)
                            }
                        }

                        // 2. Category Filter Section (NEW ADDITION)
                        item {
                            Column(modifier = Modifier.background(SurfaceBackground)) {
                                Text(
                                    text = "Explore Funds",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                CategoryFilterSection(
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { selectedCategory = it }
                                )
                            }
                        }

                        // 3. The Filtered List
                        if (filteredList.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No funds found in this category", color = TextSecondary)
                                }
                            }
                        } else {
                            items(filteredList) { scheme ->
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
}

// --- NEW COMPONENT: Horizontal Category Chips ---
@Composable
fun CategoryFilterSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Large Cap", "Mid Cap", "Small Cap", "Index Funds", "Gold")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50)) // Fully rounded pill shape
                    .background(if (isSelected) PrimaryBlue else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryBlue else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
        }
    }
}

// --- Top Movers (Gainers/Losers) ---
@Composable
fun TopMoversSection(schemes: List<SchemeDto>, onFundClick: (Int) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Gainers, 1 = Losers

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoverTab(text = "Top Gainers", isSelected = selectedTab == 0) { selectedTab = 0 }
            Spacer(modifier = Modifier.width(12.dp))
            MoverTab(text = "Top Losers", isSelected = selectedTab == 1) { selectedTab = 1 }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val displayItems = if (selectedTab == 0) schemes.take(4) else schemes.drop(4).take(4)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (displayItems.isNotEmpty()) Box(modifier = Modifier.weight(1f)) { MiniFundCard(displayItems[0], selectedTab == 0, onFundClick) }
                if (displayItems.size > 1) Box(modifier = Modifier.weight(1f)) { MiniFundCard(displayItems[1], selectedTab == 0, onFundClick) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (displayItems.size > 2) Box(modifier = Modifier.weight(1f)) { MiniFundCard(displayItems[2], selectedTab == 0, onFundClick) }
                if (displayItems.size > 3) Box(modifier = Modifier.weight(1f)) { MiniFundCard(displayItems[3], selectedTab == 0, onFundClick) }
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
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) PrimaryBlue else TextSecondary)
    }
}

@Composable
fun MiniFundCard(scheme: SchemeDto, isGainer: Boolean, onClick: (Int) -> Unit) {
    val returnVal = remember { Random.nextDouble(1.0, 10.0) }
    val sign = if (isGainer) "+" else "-"
    val color = if (isGainer) PositiveGreen else NegativeRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick(scheme.schemeCode) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = scheme.schemeName ?: "Fund", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, lineHeight = 16.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(text = "₹${scheme.nav ?: "N/A"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = "$sign${String.format("%.2f", returnVal)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun FundCard(scheme: SchemeDto, onClick: (Int) -> Unit) {
    val mockReturn = remember { Random.nextDouble(-5.0, 15.0) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(scheme.schemeCode) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = scheme.schemeName ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = scheme.fundHouse ?: "Mutual Fund", fontSize = 12.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "₹${scheme.nav ?: "N/A"}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                val isPositive = mockReturn >= 0
                Text(text = "${if (isPositive) "+" else ""}${String.format("%.2f", mockReturn)}%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isPositive) PositiveGreen else NegativeRed)
            }
        }
    }
}