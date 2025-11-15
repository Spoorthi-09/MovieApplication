package com.example.movierecommendation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.movierecommendation.R
import com.example.movierecommendation.data.remote.dto.TmdbMovieDto
import com.example.movierecommendation.ui.theme.MovieRecommendationTheme

const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"

@SuppressLint("DefaultLocale")
@Composable
fun MovieCard(movie: TmdbMovieDto, onMovieClicked: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onMovieClicked(movie.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                val painter = if (LocalInspectionMode.current) {
                    painterResource(id = R.drawable.placeholder)
                } else {
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = "$POSTER_BASE_URL${movie.poster_path}")
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                                placeholder(R.drawable.placeholder)
                                error(R.drawable.placeholder)
                            }).build()
                    )
                }
                Image(
                    painter = painter,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .clip(CircleShape)
                        .clickable { /* TODO: Add to watchlist */ }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Add to Watchlist",
                        tint = Color.White
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = movie.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", movie.vote_average),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = movie.release_date?.take(4) ?: "", // Show only the year
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieCardPreview() {
    val movie = TmdbMovieDto(
        id = 1,
        title = "Dune: Part Two",
        overview = "Follow the mythic journey of Paul Atreides...",
        poster_path = "/5aGha7Tb9FjVPCGGC4HkBS4eh9O.jpg",
        backdrop_path = "/xOMo8BRK7PfgZbpfsL5g9jRGYww.jpg",
        release_date = "2024-02-27",
        vote_average = 8.345,
        vote_count = 1000,
        popularity = 1.0,
        genre_ids = listOf(878, 12)
    )
    MovieRecommendationTheme {
        MovieCard(movie = movie, onMovieClicked = {})
    }
}
