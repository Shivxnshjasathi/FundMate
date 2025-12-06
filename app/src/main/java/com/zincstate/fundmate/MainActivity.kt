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
import com.zincstate.fundmate.ui.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // HOME SCREEN
        composable("home") {
            HomeScreen(onFundClick = { code ->
                navController.navigate("detail/$code")
            })
        }

        // DETAIL SCREEN
        composable(
            route = "detail/{code}",
            arguments = listOf(navArgument("code") { type = NavType.IntType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getInt("code") ?: 0
            Text("Detail Screen for Code: $code")
        }
    }
}
