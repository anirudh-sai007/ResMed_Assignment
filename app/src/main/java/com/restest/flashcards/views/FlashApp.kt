package com.restest.flashcards.views

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.restest.flashcards.views.navigation.FlashNavGraph

@Composable
fun FlashApp() {
    val navController = rememberNavController()
    FlashNavGraph(navController)
}