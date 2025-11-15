package com.example.movierecommendation.data.mapper

import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieGenreCrossRef
import com.example.movierecommendation.data.remote.dto.TmdbGenreDto
import com.example.movierecommendation.data.remote.dto.TmdbMovieDto

fun TmdbMovieDto.toEntity(now: Long = System.currentTimeMillis()) = MovieEntity(
    id = id,
    title = title ?: "Untitled",
    overview = overview ?: "No description available.",
    posterPath = poster_path ?: "",
    backdropPath = backdrop_path ?: "",
    releaseDate = release_date ?: "",
    voteAverage = vote_average ?: 0.0,
    voteCount = vote_count ?: 0,
    popularity = popularity ?: 0.0,
    lastUpdated = now
)

fun List<TmdbGenreDto>.toEntities() = map { GenreEntity(id = it.id, name = it.name ?: "Unknown") }

fun movieGenreRefs(movieId: Long, genreIds: List<Int>) =
    genreIds.map { gid -> MovieGenreCrossRef(movieId, gid) }