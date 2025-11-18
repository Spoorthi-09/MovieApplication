package com.example.movierecommendation.ui.popularMovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierecommendation.data.remote.util.NetworkResult
import com.example.movierecommendation.data.repository.MovieRepository
import com.example.movierecommendation.ui.model.MovieUiModel
import com.example.movierecommendation.ui.util.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class PopularMoviesUiState(
    val isLoading: Boolean = false,
    val popular: List<MovieUiModel> = emptyList(),
    val upcoming: List<MovieUiModel> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularMoviesUiState(isLoading = true))
    val uiState: StateFlow<PopularMoviesUiState> = _uiState.asStateFlow()

    private val todayIso: String = LocalDate.now().toString()

    init {
        observeMovies()
        refresh()
    }

    private fun observeMovies() {
        viewModelScope.launch {
            combine(
                repository.popular(),
                repository.upcoming(todayIso)
            ) { popularEntities, upcomingEntities ->
                PopularMoviesUiState(
                    isLoading = false,
                    popular = popularEntities.map { it.toUiModel() },
                    upcoming = upcomingEntities.map { it.toUiModel() },
                    errorMessage = null
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val popularResult = repository.refreshPopular()
            val upcomingResult = repository.refreshUpcoming()
            val genresResult = repository.refreshGenres()

            val error = listOf(popularResult, upcomingResult, genresResult)
                .filterIsInstance<NetworkResult.Error>()
                .firstOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = error?.message
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
