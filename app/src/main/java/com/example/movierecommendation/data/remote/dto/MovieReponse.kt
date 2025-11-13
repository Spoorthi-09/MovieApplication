package com.example.movierecommendation.data.remote.dto

data class MovieResponse(
    val page: Int,
    val results: List<TmdbMovieDto>,
    val total_pages: Int,
    val total_results: Int
)