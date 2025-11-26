package com.example.navaplayer.player

import android.annotation.SuppressLint
import com.example.navaplayer.data.model.Audio

data class PlayerState(
    // آهنگ فعلی
    val currentAudio: Audio? = null,
    // وضعیت پخش (Play/Pause)
    val isPlaying: Boolean = false,
    // آیا پلیر اصلا آماده پخش هست؟
    val isReady: Boolean = false,
    //  نوار پیشرفت
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
)

// تابع کمکی برای تبدیل میلی‌ثانیه به فرمت 00:00
@SuppressLint("DefaultLocale")
fun formatTime(ms: Long): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d", minutes, remainingSeconds)
}
