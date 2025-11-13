package com.example.movierecommendation.data.repository

import com.example.movierecommendation.data.local.dao.GenreDao
import com.example.movierecommendation.data.local.dao.MovieDao
import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieGenreCrossRef
import com.example.movierecommendation.data.local.entity.MovieWithGenres
import com.example.movierecommendation.data.remote.api.TmdbApi
import com.example.movierecommendation.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: TmdbApi,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao
) {
    // --- Offline streams (UI always reads these)
    fun popular(): Flow<List<MovieEntity>> = movieDao.observePopular()

    fun upcoming(todayIso: String): Flow<List<MovieEntity>> =
        movieDao.observeUpcoming(todayIso)

    fun byGenre(genreId: Int): Flow<List<MovieEntity>> =
        movieDao.observeByGenre(genreId)

    fun getWatchlist(): Flow<List<MovieWithGenres>> = movieDao.watchlist()

    // --- One-shot refreshers (call from ViewModel init / pull-to-refresh)
    suspend fun refreshPopular(apiKey: String) {
        val res = api.getPopularMovies(apiKey)
        val now = System.currentTimeMillis()
        val movies = res.results.map { it.toEntity(now) }
        val refs = res.results.flatMap { dto -> movieGenreRefs(dto.id, dto.genre_ids) }

        movieDao.upsertMovies(movies)
        genreDao.upsertCrossRefs(refs)
    }

    suspend fun refreshUpcoming(apiKey: String) {
        val res = api.getUpcomingMovies(apiKey)
        val now = System.currentTimeMillis()
        val movies = res.results.map { it.toEntity(now) }
        val refs = res.results.flatMap { dto -> movieGenreRefs(dto.id, dto.genre_ids) }

        movieDao.upsertMovies(movies)
        genreDao.upsertCrossRefs(refs)
    }

    suspend fun refreshGenres(apiKey: String) {
        val res = api.getGenres(apiKey)
        genreDao.upsertGenres(res.genres.map { GenreEntity(it.id, it.name) })
    }

    suspend fun refreshByGenre(apiKey: String, genreId: Int) {
        val res = api.getMoviesByGenre(genreId, apiKey)
        val now = System.currentTimeMillis()
        val movies = res.results.map { it.toEntity(now) }
        val refs = res.results.flatMap { dto -> movieGenreRefs(dto.id, dto.genre_ids) }

        movieDao.upsertMovies(movies)
        genreDao.upsertCrossRefs(refs)
    }

    suspend fun toggleWatchlist(id: Long, watch: Boolean) {
        movieDao.setWatchlist(id, watch)
    }
}

private fun movieGenreRefs(movieId: Long, genreIds: List<Int>) =
    genreIds.map { gid -> MovieGenreCrossRef(movieId = movieId, genreId = gid) }

