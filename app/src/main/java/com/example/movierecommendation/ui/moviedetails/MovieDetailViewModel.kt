package com.example.movierecommendation.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierecommendation.data.repository.MovieRepository
import com.example.movierecommendation.ui.model.MovieDetailUiModel
import com.example.movierecommendation.ui.util.toDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetailUiModel? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Long = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(MovieDetailUiState(isLoading = true))
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        observeMovie()
    }

    private fun observeMovie() {
        viewModelScope.launch {
            repository.observeMovie(movieId)   // Flow<MovieWithGenres?>
                .collectLatest { movieWithGenres ->
                    _uiState.value = when (movieWithGenres) {
                        null -> MovieDetailUiState(
                            isLoading = false,
                            movie = null,
                            errorMessage = "Movie not found"
                        )
                        else -> MovieDetailUiState(
                            isLoading = false,
                            movie = movieWithGenres.toDetailUiModel(),
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun toggleWatchlist(newValue: Boolean) {
        viewModelScope.launch {
            repository.toggleWatchlist(movieId, newValue)
        }
    }
}



