package com.example.navaplayer.ui.screens.player


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(navController: NavController) {
    val viewModel: PlayerViewModel = koinViewModel()
    val state by viewModel.playerState.collectAsState()
    val audio = state.currentAudio

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = audio?.title ?: "Nava Player") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder برای کاور موزیک
            Card(
                modifier = Modifier.size(250.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("کاور آهنگ", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // اطلاعات آهنگ
            Text(text = audio?.title ?: "بدون عنوان", style = MaterialTheme.typography.headlineMedium)
            Text(text = audio?.artist ?: "خواننده ناشناس", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(40.dp))

            // دکمه‌های کنترل (Play/Pause)
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // دکمه Prev و Next رو بعداً اضافه می‌کنیم

                // دکمه Play/Pause بزرگ
                Button(
                    onClick = { viewModel.togglePlayback() },
                    enabled = state.isReady,
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}