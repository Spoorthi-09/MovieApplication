package com.example.movierecommendation.data.repository

import com.example.movierecommendation.data.local.dao.GenreDao
import com.example.movierecommendation.data.local.dao.MovieDao
import com.example.movierecommendation.data.local.entity.MovieEntity
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
    suspend fun getPopularMovies(apiKey: String): List<MovieEntity> {
        val response = api.getPopularMovies(apiKey)
        val movies = response.results.map { it.toEntity() }
        movieDao.upsertMovies(movies)
        return movies
    }

    fun getWatchlist(): Flow<List<MovieWithGenres>> = movieDao.watchlist()

    suspend fun toggleWatchlist(id: Long, watch: Boolean) {
        movieDao.setWatchlist(id, watch)
    }
}
