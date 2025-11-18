package com.example.movierecommendation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieGenreCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Query("SELECT * FROM genres ORDER BY name")
    fun observeGenres(): Flow<List<GenreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrossRefs(refs: List<MovieGenreCrossRef>)
}