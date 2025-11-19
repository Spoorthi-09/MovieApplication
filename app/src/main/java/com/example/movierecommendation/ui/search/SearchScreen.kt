package com.example.movierecommendation.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                title = { Text("Search for Movies") }
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

            // Search bar (updates ViewModel)
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search by movie or genre…") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth()
            )

            // Genres dropdown
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
                GenrePicker(
                    genres = uiState.genres,
                    selectedId = uiState.selectedGenreId,
                    onSelected = { viewModel.onGenreSelected(it) }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Movies area (search has priority over genre)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Decide which list to render
                val moviesToShow =
                    if (uiState.isSearching && uiState.searchQuery.isNotBlank())
                        uiState.searchResults
                    else
                        uiState.movies

                when {
                    // If searching, ignore genre selection and show search results
                    uiState.isSearching && uiState.searchQuery.isNotBlank() -> {
                        if (moviesToShow.isEmpty()) {
                            Text(
                                text = "No results for \"${uiState.searchQuery}\".",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            MoviesForGenreList(
                                movies = moviesToShow,
                                onMovieClicked = onMovieClicked,
                                onToggleWatchlist = { id, newValue ->
                                    viewModel.toggleWatchlist(id, newValue)
                                }
                            )
                        }
                    }

                    // Not searching → fall back to genre flow
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
                            movies = moviesToShow, // here this is uiState.movies
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

@Composable
private fun GenrePicker(
    genres: List<GenreUiModel>,
    selectedId: Int?,
    onSelected: (GenreUiModel) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Genre",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val selectedGenre = genres.firstOrNull { it.id == selectedId }

                Text(
                    text = selectedGenre?.name ?: "Select a genre",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Choose a genre") },
            text = {
                LazyColumn {
                    items(genres) { genre ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected(genre)   // pass whole model
                                    showDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = genre.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            if (genre.id == selectedId) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
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