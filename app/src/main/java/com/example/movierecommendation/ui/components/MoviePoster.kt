package com.example.movierecommendation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.movierecommendation.R

const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"

@Composable
fun MoviePoster(
    posterPath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val painter = if (LocalInspectionMode.current) {
        painterResource(id = R.drawable.placeholder)
    } else {
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(data = "$POSTER_BASE_URL$posterPath")
                .apply {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)
                }.build()
        )
    }

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}