package com.example.navaplayer.ui.screens.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navaplayer.data.model.Audio
import com.example.navaplayer.data.repository.AudioRepository
import com.example.navaplayer.player.PlayerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AudioRepository,
    private val playerController: PlayerController
) : ViewModel() {

    // استفاده از mutableStateListOf برای اینکه Compose تغییرات لیست رو بفهمه
    private val _audioList = mutableStateListOf<Audio>()
    val audioList: List<Audio> get() = _audioList

    init {
        loadAudioFiles()
    }

    fun loadAudioFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val audios = repository.getAudioFiles()
            // تغییرات UI باید در ترد اصلی باشه، اما چون stateList توی کامپوز thread-safe هست مشکلی نیست
            // ولی برای اطمینان و پرفورمنس:
            launch(Dispatchers.Main) {
                _audioList.clear()
                _audioList.addAll(audios)
            }
        }
    }

    // متد جدید برای پخش
    fun playAudio(
        audioList: List<Audio>,
        startIndex: Int
    ) {
        playerController.playPlaylist(audioList, startIndex)
    }

    override fun onCleared() {
        super.onCleared()
        // وقتی ViewModel نابود شد، اتصال کنترلر رو قطع می‌کنیم تا مموری لیک نشه
        playerController.release()
    }
}