package com.example.movierecommendation.data.repository

import androidx.room.withTransaction
import com.example.movierecommendation.data.local.AppDatabase
import com.example.movierecommendation.data.local.dao.GenreDao
import com.example.movierecommendation.data.local.dao.MovieDao
import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieGenreCrossRef
import com.example.movierecommendation.data.local.entity.MovieWithGenres
import com.example.movierecommendation.data.mapper.toEntity
import com.example.movierecommendation.data.remote.api.TmdbApi
import com.example.movierecommendation.data.remote.util.NetworkResult
import com.example.movierecommendation.data.remote.util.safeApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: TmdbApi,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val db: AppDatabase
) {
    fun popular(): Flow<List<MovieEntity>> = movieDao.observePopular()
    fun upcoming(todayIso: String): Flow<List<MovieEntity>> = movieDao.observeUpcoming(todayIso)
    fun byGenre(genreId: Int): Flow<List<MovieEntity>> = movieDao.observeByGenre(genreId)
    fun getWatchlist(): Flow<List<MovieWithGenres>> = movieDao.watchlist()
    fun genres(): Flow<List<GenreEntity>> = genreDao.observeGenres()

    fun searchMovies(query: String): Flow<List<MovieWithGenres>> =
        movieDao.searchMovies(query)
    fun observeMovie(id: Long): Flow<MovieWithGenres?> =
        movieDao.observeMovie(id)
    suspend fun refreshPopular(): NetworkResult<Unit> =
        safeApi { api.getPopularMovies() }.let { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val now = System.currentTimeMillis()
                    val movies = result.data.results.map { it.toEntity(now) }
                    val refs = result.data.results.flatMap { dto ->
                        movieGenreRefs(dto.id, dto.genre_ids)
                    }
                    db.withTransaction {
                        movieDao.upsertMovies(movies)
                        genreDao.upsertCrossRefs(refs)
                    }
                    NetworkResult.Success(Unit)
                }
                is NetworkResult.Error -> result
            }
        }

    suspend fun refreshUpcoming(): NetworkResult<Unit> =
        safeApi { api.getUpcomingMovies() }.let { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val now = System.currentTimeMillis()
                    val movies = result.data.results.map { it.toEntity(now) }
                    val refs = result.data.results.flatMap { dto ->
                        movieGenreRefs(dto.id, dto.genre_ids)
                    }
                    db.withTransaction {
                        movieDao.upsertMovies(movies)
                        genreDao.upsertCrossRefs(refs)
                    }
                    NetworkResult.Success(Unit)
                }
                is NetworkResult.Error -> result
            }
        }

    suspend fun refreshGenres(): NetworkResult<Unit> =
        safeApi { api.getGenres() }.let { result ->
            when (result) {
                is NetworkResult.Success -> {
                    db.withTransaction {
                        genreDao.upsertGenres(
                            result.data.genres.map {
                                GenreEntity(
                                    id = it.id,
                                    name = it.name ?: "Unknown"
                                )
                            }
                        )
                    }
                    NetworkResult.Success(Unit)
                }
                is NetworkResult.Error -> result
            }
        }

    suspend fun refreshByGenre(genreId: Int): NetworkResult<Unit> =
        safeApi { api.getMoviesByGenre(genreId) }.let { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val now = System.currentTimeMillis()
                    val movies = result.data.results.map { it.toEntity(now) }
                    val refs = result.data.results.flatMap { dto ->
                        movieGenreRefs(dto.id, dto.genre_ids)
                    }
                    db.withTransaction {
                        movieDao.upsertMovies(movies)
                        genreDao.upsertCrossRefs(refs)
                    }
                    NetworkResult.Success(Unit)
                }
                is NetworkResult.Error -> result
            }
        }

    suspend fun toggleWatchlist(id: Long, watch: Boolean) {
        movieDao.setWatchlist(id, watch)
    }
}

private fun movieGenreRefs(movieId: Long, genreIds: List<Int>) =
    genreIds.map { gid -> MovieGenreCrossRef(movieId = movieId, genreId = gid) }
