package com.example.movierecommendation.data.remote.dto

data class MovieResponse(
    val page: Int = 1,
    val results: List<TmdbMovieDto> = emptyList(),
    val total_pages: Int = 1,
    val total_results: Int = 0
)