package com.example.movierecommendation.ui.model

data class MovieUiModel(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val isWatchlisted: Boolean
)
