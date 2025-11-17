package com.example.movierecommendation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WatchlistButton(
    isWatchlisted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isWatchlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isWatchlisted) "Remove from watchlist" else "Add to watchlist"
        )
        Spacer(Modifier.width(8.dp))
        Text(
            if (isWatchlisted) "In watchlist" else "Add to watchlist"
        )
    }
}
