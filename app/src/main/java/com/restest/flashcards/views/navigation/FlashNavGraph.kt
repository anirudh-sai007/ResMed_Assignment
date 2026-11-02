package com.restest.flashcards.views.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.restest.flashcards.views.composables.createCardRoute
import com.restest.flashcards.views.composables.flashCardPagerRoute
import com.restest.flashcards.views.composables.homeScreenRoute

@Composable
fun FlashNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        homeScreenRoute(navController = navController)
        flashCardPagerRoute(navController = navController)
        createCardRoute(navController = navController)
    }
}