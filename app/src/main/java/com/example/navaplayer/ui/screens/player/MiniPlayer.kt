package com.example.navaplayer.ui.screens.player


import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.navaplayer.ui.main.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiniPlayer(navController: NavController) {
    val viewModel: PlayerViewModel = koinViewModel()
    // مشاهده وضعیت پلیر
    val playerState by viewModel.playerState.collectAsState()

    // اگر هیچ آهنگی لود نشده باشه، مینی‌پلیر رو نمایش نده
    if (playerState.currentAudio == null) {
        return
    }

    // استفاده از AnimatedVisibility برای نمایش متحرک مینی‌پلیر
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // کلیک روی مینی‌پلیر باز کردن فول‌پلیر
                    navController.navigate(Screen.FullPlayer.route)
                }
                .padding(bottom = 0.dp),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // نام آهنگ
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text(
                        text = playerState.currentAudio?.title ?: "بدون آهنگ",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                    )
                    Text(
                        text = playerState.currentAudio?.artist ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }

                // دکمه Play/Pause
                IconButton(onClick = { viewModel.togglePlayback() }) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause"
                    )
                }
            }
        }
    }
}