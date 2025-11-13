package com.example.movierecommendation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE id = :id")
    @Transaction
    suspend fun movie(id: Long): MovieWithGenres?

    @Query("UPDATE movies SET isWatchlisted = :watch WHERE id = :id")
    suspend fun setWatchlist(id: Long, watch: Boolean)

    @Query("SELECT * FROM movies WHERE isWatchlisted = 1 ORDER BY title")
    @Transaction
    fun watchlist(): Flow<List<MovieWithGenres>>
}


