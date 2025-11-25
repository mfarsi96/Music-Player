package com.example.navaplayer.data.model

import android.net.Uri

data class Audio(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val duration: Int
)