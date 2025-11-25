package com.example.navaplayer.ui.main

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object FullPlayer : Screen("full_player_screen")
}