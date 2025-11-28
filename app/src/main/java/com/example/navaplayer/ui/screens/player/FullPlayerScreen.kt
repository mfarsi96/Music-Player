package com.example.navaplayer.ui.screens.player


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
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

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentPosition, state.duration, state.isReady) {
        if (!isSeeking && state.duration > 0) {
            sliderPosition = state.currentPosition.toFloat()
        }
    }

    // استفاده از Box برای لایه‌بندی
    Box(modifier = Modifier.fillMaxSize()) {

        // --- لایه ۱: پس‌زمینه تار (Blurred Background) ---
        AsyncImage(
            model = state.currentAudio?.coverUri,
            contentDescription = null, // توضیحات برای تصویر پس‌زمینه لازم نیست
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 0.dp) // میزان تاری را تنظیم کنید (مثلاً 70.dp)
                .alpha(0.8f) // شفافیت کم برای حس محو بودن
        )

        // --- لایه ۲: پوشش تاریک (Dark Overlay) ---
        // این لایه کنتراست را بهبود می‌بخشد تا متن‌ها خوانا بمانند
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // --- لایه ۳: محتوای اصلی (Scaffold) ---
        Scaffold(
            // **تنظیم مهم:** پس‌زمینه Scaffold باید شفاف باشد
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(text = audio?.title ?: "Nava Player") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    // پس‌زمینه نوار بالا را نیز شفاف می‌کنیم
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // --- بخش کاور و اطلاعات آهنگ ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier.size(250.dp),
                        shape = MaterialTheme.shapes.medium,
                        // کاور اصلی: پس‌زمینه باید کمی متمایز باشد
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = state.currentAudio?.coverUri,
                            contentDescription = "Album Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = audio?.title ?: "بدون عنوان", style = MaterialTheme.typography.headlineMedium, maxLines = 1, color = Color.White)
                    Text(text = audio?.artist ?: "خواننده ناشناس", style = MaterialTheme.typography.titleMedium, maxLines = 1, color = Color.LightGray)
                }

                // --- بخش کنترل پخش (قبلی) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

                    // ۱. نوار پیشرفت (Seekbar)
                    Slider(
                        value = sliderPosition,
                        onValueChange = { newPosition ->
                            isSeeking = true
                            sliderPosition = newPosition
                        },
                        onValueChangeFinished = {
                            viewModel.seekTo(sliderPosition.toLong())
                            isSeeking = false
                        },
                        valueRange = 0f..state.duration.toFloat(),
                        enabled = state.duration > 0,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                    )

                    // نمایش زمان
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formatTime(state.currentPosition), style = MaterialTheme.typography.bodySmall, color = Color.White)
                        Text(text = formatTime(state.duration), style = MaterialTheme.typography.bodySmall, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // دکمه‌های کنترل
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
                    ) {
                        // ... (دکمه‌های Skip و Play/Pause قبلی) ...
                        IconButton(onClick = { viewModel.skipToPrevious() }, enabled = state.isReady) {
                            Icon(Icons.Filled.Close, contentDescription = "Previous", modifier = Modifier.size(36.dp), tint = Color.White)
                        }

                        Button(
                            onClick = { viewModel.togglePlayback() },
                            enabled = state.isReady,
                            modifier = Modifier.size(72.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Filled.PlayArrow else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.background // رنگ آیکون تیره باشد
                            )
                        }

                        IconButton(onClick = { viewModel.skipToNext() }, enabled = state.isReady) {
                            Icon(Icons.Filled.Close, contentDescription = "Next", modifier = Modifier.size(36.dp), tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}