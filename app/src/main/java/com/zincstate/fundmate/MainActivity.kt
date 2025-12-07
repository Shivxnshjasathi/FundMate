package com.zincstate.fundmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zincstate.fundmate.ui.theme.FundmateTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.compose.material3.Text
import androidx.navigation.navArgument
import com.zincstate.fundmate.data.repository.WatchlistRepository
import com.zincstate.fundmate.ui.detail.DetailScreen
import com.zincstate.fundmate.ui.explore.AllSchemesScreen
import com.zincstate.fundmate.ui.home.HomeScreen
import com.zincstate.fundmate.ui.search.SearchScreen
import com.zincstate.fundmate.ui.watchlist.WatchlistScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WatchlistRepository.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            FundmateTheme {
                AppNavigation()
            }
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // 1. Home Route
        composable("home") {
            HomeScreen(
                onFundClick = { code -> navController.navigate("detail/$code") },
                onSearchClick = { navController.navigate("search") },
                onExploreClick = { navController.navigate("explore") },
                onWatchlistClick = { navController.navigate("watchlist") } // Added
            )
        }

        // 2. Search Route
        composable("search") {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onFundClick = { code -> navController.navigate("detail/$code") }
            )
        }

        // 3. Detail Route (Removed duplicate, kept the correct one)
        composable(
            route = "detail/{code}",
            arguments = listOf(navArgument("code") { type = NavType.IntType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getInt("code") ?: 0
            DetailScreen(
                schemeCode = code,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 4. Watchlist Route
        composable("watchlist") {
            WatchlistScreen(
                onFundClick = { code -> navController.navigate("detail/$code") }
            )
        }

        // 5. Explore Route
        composable("explore") {
            AllSchemesScreen(
                onFundClick = { code -> navController.navigate("detail/$code") }
            )
        }

    }
}

