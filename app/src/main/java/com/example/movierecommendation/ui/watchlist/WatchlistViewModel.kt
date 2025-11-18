package com.example.movierecommendation.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierecommendation.data.repository.MovieRepository
import com.example.movierecommendation.ui.model.MovieUiModel
import com.example.movierecommendation.ui.util.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import jakarta.inject.Inject

data class WatchlistUiState(
    val isLoading: Boolean = true,
    val movies: List<MovieUiModel> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState(isLoading = true))
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        observeWatchlist()
    }

    private fun observeWatchlist() {
        viewModelScope.launch {
            repository.getWatchlist()          // Flow<List<MovieWithGenres>>
                .collectLatest { moviesWithGenres ->
                    _uiState.value = WatchlistUiState(
                        isLoading = false,
                        movies = moviesWithGenres.map { it.toUiModel() },
                        errorMessage = null
                    )
                }
        }
    }

    fun toggleWatchlist(id: Long, watch: Boolean) {
        viewModelScope.launch {
            repository.toggleWatchlist(id, watch)
        }
    }
}