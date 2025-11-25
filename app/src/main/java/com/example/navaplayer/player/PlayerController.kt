package com.example.navaplayer.player


import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.navaplayer.data.model.Audio
import com.example.navaplayer.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class PlayerController(context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null


    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()


    init {
        // ساخت توکن برای وصل شدن به PlaybackService که قبلا ساختیم
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )

        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            val controller = mediaController
            if (controller != null) {
                // ۲. اضافه کردن Listener برای دریافت تغییرات ExoPlayer
                controller.addListener(PlayerEventListener())
            }
        }, MoreExecutors.directExecutor())
    }

    fun playAudio(audio: Audio) {
        val controller = mediaController ?: return

        // تبدیل مدل Audio خودمون به فرمت استاندارد MediaItem
        val mediaItem = MediaItem.Builder()
            .setUri(audio.uri)
            .setMediaId(audio.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(audio.title)
                    .setArtist(audio.artist)
                    .build()
            )
            .build()

        // دستورات پخش
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
        _playerState.update { currentState ->
            currentState.copy(
                currentAudio = audio,
                isReady = true
            )
        }
    }

    fun togglePlayback() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    fun release() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playerState.update { currentState ->
                currentState.copy(isReady = playbackState == Player.STATE_READY)
            }
        }

        // بعداً متدهای onMediaItemTransition را برای مدیریت لیست پخش اضافه می‌کنیم.
    }
}