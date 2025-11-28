package com.example.navaplayer.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.navaplayer.data.model.Audio
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val audioList = viewModel.audioList
    val context = LocalContext.current

    val permission = Manifest.permission.READ_EXTERNAL_STORAGE

    // لانچر برای درخواست مجوز
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // این بلاک بعد از انتخاب کاربر اجرا می‌شود
        if (isGranted) {
            // اگر کاربر دسترسی داد، آهنگ‌ها را بارگذاری کن
            viewModel.loadAudioFiles()
        } else {
            // اگر دسترسی رد شد، یک پیام به کاربر نشان بده (مثلاً Snackbar)
            // فعلاً فقط لاگ می‌کنیم
            println("Permission denied. Cannot load music.")
        }
    }

    // NEW: چک کردن دسترسی و بارگذاری داده در LaunchedEffect
    LaunchedEffect(Unit) {
        // ۱. بررسی وضعیت فعلی دسترسی
        val currentPermissionStatus = ContextCompat.checkSelfPermission(context, permission)

        if (currentPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            // اگر دسترسی از قبل داده شده بود، بلافاصله بارگذاری کن
            viewModel.loadAudioFiles()
        } else {
            // اگر دسترسی داده نشده بود، درخواست را اجرا کن
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Nava Player",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
Column() { }
        if (audioList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ آهنگی پیدا نشد یا مجوز داده نشده!")
                Button(
                    onClick = { permissionLauncher.launch(permission) },
                    modifier = Modifier.padding(top = 60.dp)
                ) {
                    Text("دریافت مجوز")
                }
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(audioList) { index, audio ->
                    AudioItem(audio = audio) {
                        viewModel.playAudio(
                            audioList = audioList,
                            startIndex = index
                        )
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