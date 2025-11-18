package com.example.movierecommendation.ui.util

import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieWithGenres
import com.example.movierecommendation.ui.model.GenreUiModel
import com.example.movierecommendation.ui.model.MovieDetailUiModel
import com.example.movierecommendation.ui.model.MovieUiModel

fun MovieEntity.toUiModel(): MovieUiModel =
    MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        isWatchlisted = isWatchlisted
    )

fun MovieWithGenres.toDetailUiModel(): MovieDetailUiModel {
    val movie = this.movie
    val genres = this.genres.map { it.name }

    return MovieDetailUiModel(
        id = movie.id,
        title = movie.title,
        overview = movie.overview,
        posterPath = movie.posterPath,
        backdropPath = movie.backdropPath,
        releaseDate = movie.releaseDate,
        voteAverage = movie.voteAverage,
        voteCount = movie.voteCount,
        genres = genres,
        isWatchlisted = movie.isWatchlisted
    )
}

fun MovieWithGenres.toUiModel(): MovieUiModel =
    this.movie.toUiModel()

fun GenreEntity.toUiModel(): GenreUiModel =
    GenreUiModel(
        id = id,
        name = name
    )
