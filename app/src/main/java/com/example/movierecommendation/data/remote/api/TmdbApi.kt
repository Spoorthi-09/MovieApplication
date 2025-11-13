package com.example.movierecommendation.data.remote.api

import com.example.movierecommendation.data.remote.dto.GenreResponse
import com.example.movierecommendation.data.remote.dto.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") key: String): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("api_key") key: String): MovieResponse

    @GET("genre/movie/list")
    suspend fun getGenres(@Query("api_key") key: String): GenreResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("api_key") key: String
    ): MovieResponse
}
