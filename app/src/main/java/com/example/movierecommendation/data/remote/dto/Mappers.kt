package com.example.movierecommendation.data.remote.dto

import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieGenreCrossRef

fun TmdbMovieDto.toEntity(now: Long = System.currentTimeMillis()) = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = poster_path,
    backdropPath = backdrop_path,
    releaseDate = release_date,
    voteAverage = vote_average,
    voteCount = vote_count,
    popularity = popularity,
    lastUpdated = now
)

fun List<TmdbGenreDto>.toEntities() = map { GenreEntity(it.id, it.name) }

fun movieGenreRefs(movieId: Long, genreIds: List<Int>) =
    genreIds.map { gid -> MovieGenreCrossRef(movieId, gid) }