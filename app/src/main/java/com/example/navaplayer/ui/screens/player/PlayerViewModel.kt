package com.example.navaplayer.ui.screens.player

import androidx.lifecycle.ViewModel
import com.example.navaplayer.player.PlayerController
import com.example.navaplayer.player.PlayerState
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel(private val controller: PlayerController) : ViewModel() {

    // وضعیت پلیر که UI باید آن را مشاهده کند
    val playerState: StateFlow<PlayerState> = controller.playerState

    fun togglePlayback() {
        controller.togglePlayback()
    }

    // بعداً متدهای seek و skip را اینجا اضافه می‌کنیم
}