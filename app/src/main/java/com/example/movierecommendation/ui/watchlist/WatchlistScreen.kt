package com.example.movierecommendation.ui.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movierecommendation.ui.components.MovieCard
import com.example.movierecommendation.ui.model.MovieUiModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    onMovieClicked: (Long) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlist") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.movies.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.movies.isEmpty() -> {
                    Text(
                        text = "Your watchlist is empty.\nAdd some movies to get started!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    WatchlistContent(
                        movies = uiState.movies,
                        onMovieClicked = onMovieClicked,
                        onWatchlistClicked = { id, newValue ->
                            viewModel.toggleWatchlist(id, newValue)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchlistContent(
    movies: List<MovieUiModel>,
    onMovieClicked: (Long) -> Unit,
    onWatchlistClicked: (Long, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                onMovieClicked = { onMovieClicked(movie.id) },
                onWatchlistClicked = {
                    onWatchlistClicked(movie.id, !movie.isWatchlisted)
                }
            )
        }
    }
}
