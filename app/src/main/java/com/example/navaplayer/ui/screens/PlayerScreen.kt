package com.example.navaplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen() {
    // اینجا بعداً ViewModel را وصل می‌کنیم
    // val viewModel: PlayerViewModel = koinViewModel()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // کاور موزیک (فعلاً یک باکس رنگی)
        Card(
            modifier = Modifier.size(300.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            // بعداً Image با Coil اینجا قرار می‌گیرد
        }

        Spacer(modifier = Modifier.height(32.dp))

        // نام آهنگ
        Text(text = "نام آهنگ", style = MaterialTheme.typography.headlineMedium)
        Text(text = "نام خواننده", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // دکمه‌های کنترل
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { /* Previous */ }) { Text("Prev") }

            // دکمه پخش/توقف
            Button(onClick = { /* Play/Pause */ }) { Text("Play") }

            Button(onClick = { /* Next */ }) { Text("Next") }
        }
    }
}