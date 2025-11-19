package com.example.movierecommendation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierecommendation.data.repository.MovieRepository
import com.example.movierecommendation.ui.model.GenreUiModel
import com.example.movierecommendation.ui.model.MovieUiModel
import com.example.movierecommendation.ui.util.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoadingGenres: Boolean = true,
    val genres: List<GenreUiModel> = emptyList(),
    val selectedGenreId: Int? = null,

    val isLoadingMovies: Boolean = false,
    val movies: List<MovieUiModel> = emptyList(), // from selected genre

    val errorMessage: String? = null,

    // ---- Search ----
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<MovieUiModel> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var moviesJob: Job? = null
    private var searchJob: Job? = null

    init {
        observeGenres()
    }

    private fun observeGenres() {
        viewModelScope.launch {
            repository.genres()  // Flow<List<GenreEntity>>
                .collectLatest { entities ->
                    val genreUi = entities.map { it.toUiModel() }
                    _uiState.value = _uiState.value.copy(
                        isLoadingGenres = false,
                        genres = genreUi
                    )
                }
        }
    }

    fun onGenreSelected(genre: GenreUiModel) {
        if (_uiState.value.selectedGenreId == genre.id) return

        // when user picks a genre, clear any active search
        _uiState.value = _uiState.value.copy(
            selectedGenreId = genre.id,
            isLoadingMovies = true,
            movies = emptyList(),
            errorMessage = null,
            searchQuery = "",
            isSearching = false,
            searchResults = emptyList()
        )

        // cancel previous flow if user taps another genre
        moviesJob?.cancel()
        moviesJob = viewModelScope.launch {
            // kick off network refresh for that genre
            repository.refreshByGenre(genre.id)

            // then observe local DB for that genre
            repository.byGenre(genre.id)  // Flow<List<MovieEntity>>
                .collectLatest { entities ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMovies = false,
                        movies = entities.map { it.toUiModel() }
                    )
                }
        }
    }

    // 🔍 Search by movie title OR genre name
    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            isSearching = query.isNotBlank()
        )

        // cancel previous search flow
        searchJob?.cancel()

        if (query.isBlank()) {
            // clear search → fall back to genre-based list
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                searchResults = emptyList()
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L)
            repository.searchMovies(query)   // Flow<List<MovieEntity>>
                .collectLatest { entities ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = entities.map { it.toUiModel() }
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
