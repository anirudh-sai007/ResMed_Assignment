package com.restest.flashcards.views.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object FlashCards : Screen("flashcards")
    data object CreateCard : Screen("createcard")
}