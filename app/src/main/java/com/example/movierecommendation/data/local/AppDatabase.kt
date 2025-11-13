package com.example.movierecommendation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movierecommendation.data.local.dao.GenreDao
import com.example.movierecommendation.data.local.dao.MovieDao
import com.example.movierecommendation.data.local.entity.GenreEntity
import com.example.movierecommendation.data.local.entity.MovieEntity
import com.example.movierecommendation.data.local.entity.MovieGenreCrossRef

@Database(
    entities = [MovieEntity::class, GenreEntity::class, MovieGenreCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
}
