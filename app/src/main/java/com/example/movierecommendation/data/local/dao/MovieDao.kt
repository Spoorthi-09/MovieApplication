package com.example.movierecommendation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieWithGenres
import kotlinx.coroutines.flow.Flow

// MovieDao.kt
@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE id = :id")
    @Transaction
    suspend fun movie(id: Long): MovieWithGenres?

    @Query("SELECT * FROM movies WHERE id = :id")
    @Transaction
    fun observeMovie(id: Long): Flow<MovieWithGenres?>

    @Query("UPDATE movies SET isWatchlisted = :watch WHERE id = :id")
    suspend fun setWatchlist(id: Long, watch: Boolean)

    @Query("SELECT * FROM movies WHERE isWatchlisted = 1 ORDER BY title")
    @Transaction
    fun watchlist(): Flow<List<MovieWithGenres>>

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun observePopular(): Flow<List<MovieEntity>>

    @Query("""
        SELECT * FROM movies 
        WHERE releaseDate IS NOT NULL AND releaseDate >= :today 
        ORDER BY releaseDate ASC
    """)
    fun observeUpcoming(today: String): Flow<List<MovieEntity>>

    @Query("""
        SELECT m.* FROM movies m 
        INNER JOIN movie_genre mg ON m.id = mg.movieId
        WHERE mg.genreId = :genreId
        ORDER BY m.popularity DESC
    """)
    fun observeByGenre(genreId: Int): Flow<List<MovieEntity>>

    @Query("""
        SELECT * FROM movies 
        WHERE title LIKE :q OR overview LIKE :q
        ORDER BY popularity DESC
    """)
    fun searchOffline(q: String): Flow<List<MovieEntity>>
}



