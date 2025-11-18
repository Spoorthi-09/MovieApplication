package com.example.movierecommendation.ui.popularMovies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movierecommendation.ui.components.MovieCard
import com.example.movierecommendation.ui.model.MovieUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularMoviesScreen(
    onMovieClicked: (Long) -> Unit,
    viewModel: PopularMoviesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular & Upcoming") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.popular.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.popular.isEmpty() && uiState.upcoming.isEmpty() -> {
                    Text(
                        text = "No movies yet.\nPull to refresh or check your connection.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    PopularMoviesContent(
                        popular = uiState.popular,
                        upcoming = uiState.upcoming,
                        onMovieClicked = onMovieClicked,
                        onWatchlistClicked = { id, newValue ->
                            viewModel.toggleWatchlist(id, newValue)
                        }
                    )
                }
            }

            uiState.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun PopularMoviesContent(
    popular: List<MovieUiModel>,
    upcoming: List<MovieUiModel>,
    onMovieClicked: (Long) -> Unit,
    onWatchlistClicked: (movieId: Long, newValue: Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (popular.isNotEmpty()) {
            item { SectionHeader("Popular movies") }
            items(popular, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    onMovieClicked = { onMovieClicked(movie.id) },
                    onWatchlistClicked = {
                        onWatchlistClicked(movie.id, !movie.isWatchlisted)
                    }
                )
            }
        }

        if (upcoming.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader("Upcoming movies")
            }
            items(upcoming, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    onMovieClicked = { onMovieClicked(movie.id) },
                    onWatchlistClicked = {
                        onWatchlistClicked(movie.id, !movie.isWatchlisted)
                    }
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}



@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}