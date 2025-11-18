
package com.example.movierecommendation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.movierecommendation.ui.components.BottomNavBar
import com.example.movierecommendation.ui.navigation.AppNavHost
import com.example.movierecommendation.ui.navigation.BottomNavItem

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Watchlist,
    )

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                items = items
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


