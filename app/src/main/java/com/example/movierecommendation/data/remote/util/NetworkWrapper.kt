package com.example.movierecommendation.data.remote.util

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T): NetworkResult<T>()
    data class Error(val message: String): NetworkResult<Nothing>()
}

suspend inline fun <T> safeApi(noinline call: suspend () -> T): NetworkResult<T> =
    try { NetworkResult.Success(call()) }
    catch (e: Exception) { NetworkResult.Error(e.localizedMessage ?: "Network error") }
