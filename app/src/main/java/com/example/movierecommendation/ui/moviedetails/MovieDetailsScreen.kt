package com.example.movierecommendation.ui.moviedetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movierecommendation.ui.components.MovieDetailCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.movie?.title ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1) Loading
            if (uiState.isLoading && uiState.movie == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 2) Error (only when we don’t have a movie)
            if (uiState.errorMessage != null && uiState.movie == null) {
                val msg = uiState.errorMessage
                Text(
                    text = msg.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 3) Content
            uiState.movie?.let { movie ->
                MovieDetailCard(
                    movie = movie,
                    onToggleWatchlist = { newValue ->
                        viewModel.toggleWatchlist(newValue)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}