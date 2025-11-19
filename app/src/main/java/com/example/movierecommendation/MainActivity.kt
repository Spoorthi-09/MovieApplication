package com.example.movierecommendation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.movierecommendation.ui.screens.MainScreen
import com.example.movierecommendation.ui.theme.MovieRecommendationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieRecommendationTheme(
                darkTheme = true,      // force dark
                dynamicColor = false
            ) {
                MainScreen()
            }
        }
    }
}
