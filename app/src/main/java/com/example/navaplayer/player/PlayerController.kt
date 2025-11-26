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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PlayerController(context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null


    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

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
                controller.addListener(PlayerEventListener())
                // شروع به‌روزرسانی موقعیت پخش به محض اتصال کنترلر
                startProgressUpdate()
            }
        }, MoreExecutors.directExecutor())
    }

    fun playPlaylist(audioList: List<Audio>, startIndex: Int) {
        val controller = mediaController ?: return

        // ۱. تبدیل لیست Audio به لیست MediaItem
        val mediaItems = audioList.map { audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaId(audio.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(audio.title)
                        .setArtist(audio.artist)
                        .setSubtitle(audio.artist) // می‌توان از ساب‌تایتل برای Artist استفاده کرد
                        .build()
                )
                .build()
        }

        // ۲. ارسال کل لیست به پلیر و مشخص کردن آهنگ شروع
        controller.setMediaItems(mediaItems, startIndex, 0)

        // ۳. دستور پخش
        controller.prepare()
        controller.play()

        // ۴. آپدیت فوری وضعیت UI
        _playerState.update { currentState ->
            currentState.copy(
                currentAudio = audioList[startIndex],
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

    // متد جدید: پرش به آهنگ بعدی
    fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }

    // متد جدید: پرش به آهنگ قبلی
    fun skipToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    // متد جدید: پرش به موقعیت مشخص در آهنگ (برای نوار پیشرفت)
    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        // موقعیت رو بلافاصله در Flow آپدیت می‌کنیم تا UI سریع واکنش نشون بده
        _playerState.update { it.copy(currentPosition = positionMs) }
    }


    fun release() {
        progressJob?.cancel() // قطع کردن لوپ به‌روزرسانی
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private fun startProgressUpdate() {
        progressJob?.cancel() // اگر از قبل بود، کنسلش کن
        progressJob = scope.launch {
            while (true) {
                val controller = mediaController
                val currentPosition = controller?.currentPosition ?: 0L
                val duration = controller?.duration ?: 0L

                // آپدیت وضعیت در Flow
                _playerState.update { currentState ->
                    currentState.copy(
                        currentPosition = if (currentPosition < 0) 0L else currentPosition,
                        duration = if (duration <= 0) 0L else duration
                    )
                }

                delay(1000) // هر یک ثانیه یک بار آپدیت کن
            }
        }
    }
    private inner class PlayerEventListener : Player.Listener {


        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val controller = mediaController ?: return

            // پیدا کردن آهنگ جدید از روی متادیتا و به‌روزرسانی وضعیت
            val title = mediaItem?.mediaMetadata?.title?.toString()
            val artist = mediaItem?.mediaMetadata?.artist?.toString()
            val id = mediaItem?.mediaId?.toLongOrNull()

            // فرض می‌کنیم اگر اطلاعات کامل بود، یک آبجکت Audio بسازیم (ایده‌آل نیست ولی برای شروع کار میکنه)
            if (title != null && artist != null && id != null) {
                // اگر می‌خواهید URI را هم به‌روز کنید، باید از controller.getCurrentMediaItem() استفاده کنید.
                _playerState.update { currentState ->
                    currentState.copy(
                        currentAudio = Audio(
                            id = id,
                            uri = mediaItem.requestMetadata.mediaUri ?: mediaItem.mediaId.let { /* fallback logic */ } as android.net.Uri, // URI دقیق باید استخراج شود
                            title = title,
                            artist = artist,
                            duration = controller.duration.toInt()
                        )
                    )
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playerState.update { currentState ->
                currentState.copy(isReady = playbackState == Player.STATE_READY)
            }
        }

        override fun onPlayerErrorChanged(error: androidx.media3.common.PlaybackException?) {
            // در صورت بروز خطا در پخش
            println("Player Error: ${error?.message}")
        }
        // بعداً متدهای onMediaItemTransition را برای مدیریت لیست پخش اضافه می‌کنیم.
    }
}