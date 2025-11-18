package com.example.movierecommendation.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movierecommendation.ui.components.MovieCard
import com.example.movierecommendation.ui.model.GenreUiModel
import com.example.movierecommendation.ui.model.MovieUiModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onMovieClicked: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse by genre") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fake search bar for now (non-functional)
            OutlinedTextField(
                value = "",
                onValueChange = { /* do later */ },
                enabled = false,
                placeholder = { Text("Search… (coming soon)") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Genres row / chips
            Text(
                text = "Genres",
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.isLoadingGenres && uiState.genres.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.genres.isEmpty()) {
                Text(
                    text = "No genres available.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                GenreChipsRow(
                    genres = uiState.genres,
                    selectedId = uiState.selectedGenreId,
                    onSelected = { viewModel.onGenreSelected(it) }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Movies for selected genre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.selectedGenreId == null -> {
                        Text(
                            text = "Tap a genre to see movies.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.isLoadingMovies && uiState.movies.isEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.movies.isEmpty() -> {
                        Text(
                            text = "No movies found for this genre.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        MoviesForGenreList(
                            movies = uiState.movies,
                            onMovieClicked = onMovieClicked,
                            onToggleWatchlist = { id, newValue ->
                                viewModel.toggleWatchlist(id, newValue)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreChipsRow(
    genres: List<GenreUiModel>,
    selectedId: Int?,
    onSelected: (GenreUiModel) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            FilterChip(
                selected = genre.id == selectedId,
                onClick = { onSelected(genre) },
                label = {
                    Text(
                        text = genre.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun MoviesForGenreList(
    movies: List<MovieUiModel>,
    onMovieClicked: (Long) -> Unit,
    onToggleWatchlist: (Long, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                onMovieClicked = { onMovieClicked(movie.id) },
                onWatchlistClicked = {
                    onToggleWatchlist(movie.id, !movie.isWatchlisted)
                }
            )
        }
    }
}