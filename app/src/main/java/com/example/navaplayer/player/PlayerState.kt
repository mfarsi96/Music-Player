package com.example.navaplayer.player

import com.example.navaplayer.data.model.Audio

data class PlayerState(
    // آهنگ فعلی
    val currentAudio: Audio? = null,
    // وضعیت پخش (Play/Pause)
    val isPlaying: Boolean = false,
    // آیا پلیر اصلا آماده پخش هست؟
    val isReady: Boolean = false,
    // بعداً می‌توانیم پوزیشن و مدت زمان را اینجا اضافه کنیم
)
