package com.example.navaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navaplayer.ui.screens.home.HomeScreen
import com.example.navaplayer.ui.theme.NavaPlayerTheme
import com.example.navaplayer.ui.main.Screen
import com.example.navaplayer.ui.screens.player.FullPlayerScreen
import com.example.navaplayer.ui.screens.player.MiniPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavaPlayerTheme {
                NavaPlayer()
            }
        }
    }
}

@Composable
fun NavaPlayer() {
    val navController = rememberNavController()

    Scaffold(
        // استفاده از BottomBar برای نگه داشتن مینی‌پلیر در تمام مسیرها
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            MiniPlayer(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.FullPlayer.route) {
                FullPlayerScreen(navController = navController)
            }
        }
    }
}