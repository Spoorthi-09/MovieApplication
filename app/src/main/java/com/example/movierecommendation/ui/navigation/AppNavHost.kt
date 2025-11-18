package com.example.movierecommendation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movierecommendation.ui.moviedetails.MovieDetailsScreen
import com.example.movierecommendation.ui.popularMovies.PopularMoviesScreen
import com.example.movierecommendation.ui.search.SearchScreen
import com.example.movierecommendation.ui.watchlist.WatchlistScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        // Home tab
        composable(BottomNavItem.Home.route) {
            PopularMoviesScreen(
                onMovieClicked = { movieId ->
                    navController.navigate("details/$movieId")
                }
            )
        }

        // Search tab
        composable(BottomNavItem.Search.route) {
            SearchScreen(
                onMovieClicked = { movieId ->
                    navController.navigate("details/$movieId")
                }
            )
        }

        // Watchlist tab
        composable(BottomNavItem.Watchlist.route) {
            WatchlistScreen(
                onMovieClicked = { movieId ->
                    navController.navigate("details/$movieId")
                }
            )
        }

        // Details screen (no bottom-nav item, just a destination)
        composable(
            route = "details/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.LongType
                }
            )
        ) {
            MovieDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}