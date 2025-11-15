package com.example.movierecommendation.data.remote.dto

data class GenreResponse(
    val genres: List<TmdbGenreDto> = emptyList()
)