package com.example.navaplayer.ui.screens.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.navaplayer.data.model.Audio
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val audioList = viewModel.audioList
    val context = LocalContext.current

    // تشخیص نوع مجوز بر اساس نسخه اندروید
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // لانچر برای درخواست مجوز
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadAudioFiles()
        }
    }

    // بررسی اولیه (SideEffect برای اینکه فقط یک بار اجرا بشه)
    LaunchedEffect(Unit) {
        // اینجا ساده گرفتیم، در واقعیت باید چک کنی که مجوز داری یا نه
        // فعلاً فرض میکنیم اگر لیست خالیه درخواست بدیم
        launcher.launch(permission)
    }

    Scaffold(
        topBar = {
            // هدر ساده
            Text(
                text = "Nava Player",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->

        if (audioList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ آهنگی پیدا نشد یا مجوز داده نشده!")
                // دکمه تلاش مجدد رو هم میشه اینجا گذاشت
                Button(onClick = { launcher.launch(permission) }, modifier = Modifier.padding(top = 60.dp)) {
                    Text("دریافت مجوز")
                }
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {
                items(audioList) { audio ->
                    AudioItem(audio = audio) {
                        viewModel.playAudio(audio)
                    }
                }
            }
        }
    }
}

@Composable
fun AudioItem(audio: Audio, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // آیکون موزیک (جایگزین کاور فعلی)
            // می‌تونی بعدا از Coil استفاده کنی
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🎵")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = audio.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = audio.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}