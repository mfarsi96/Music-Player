package com.example.navaplayer.ui.screens.player


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.navaplayer.player.formatTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(navController: NavController) {
    val viewModel: PlayerViewModel = koinViewModel()
    val state by viewModel.playerState.collectAsState()
    val audio = state.currentAudio

    // وضعیت داخلی برای Seekbar: چون Drag کردن Seekbar نباید هر لحظه ViewModel رو آپدیت کنه.
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    // اگر در حال Drag کردن هستیم
    var isSeeking by remember { mutableStateOf(false) }

    // هماهنگ‌سازی موقعیت Slider با پلیر (فقط وقتی در حال Drag نیستیم)
    LaunchedEffect(state.currentPosition, state.duration, state.isReady) {
        if (!isSeeking && state.duration > 0) {
            sliderPosition = state.currentPosition.toFloat()
        }
    }


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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder برای کاور موزیک
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier.size(250.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    // **جایگزینی Placeholder با AsyncImage از Coil**
                    AsyncImage(
                        model = state.currentAudio?.coverUri, // آدرس URI کاور
                        contentDescription = "Album Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // اطلاعات آهنگ
            Text(
                text = audio?.title ?: "بدون عنوان",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = audio?.artist ?: "خواننده ناشناس",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // دکمه‌های کنترل (Play/Pause)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                // ۱. نوار پیشرفت (Seekbar)
                Slider(
                    value = sliderPosition,
                    onValueChange = { newPosition ->
                        isSeeking = true
                        sliderPosition = newPosition
                    },
                    onValueChangeFinished = {
                        // وقتی درگ کردن تمام شد، موقعیت رو به پلیر بفرست
                        viewModel.seekTo(sliderPosition.toLong())
                        isSeeking = false
                    },
                    valueRange = 0f..state.duration.toFloat(),
                    enabled = state.duration > 0,
                    modifier = Modifier.fillMaxWidth()
                )

                // نمایش زمان
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(state.currentPosition),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatTime(state.duration),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ۲. دکمه‌های کنترل (Prev, Play/Pause, Next)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    // دکمه قبلی
                    IconButton(onClick = { viewModel.skipToPrevious() }, enabled = state.isReady) {
                        Icon(
                            Icons.Filled.Phone,
                            contentDescription = "Previous",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // دکمه Play/Pause بزرگ
                    Button(
                        onClick = { viewModel.togglePlayback() },
                        enabled = state.isReady,
                        modifier = Modifier.size(72.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // دکمه بعدی
                    IconButton(onClick = { viewModel.skipToNext() }, enabled = state.isReady) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Next",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}