package com.example.movierecommendation.data.remote.dto

data class TmdbMovieDto(
    val id: Long,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String?,
    val vote_average: Double,
    val vote_count: Int,
    val popularity: Double,
    val genre_ids: List<Int> = emptyList()
)