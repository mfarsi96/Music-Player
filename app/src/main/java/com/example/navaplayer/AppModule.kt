package com.example.navaplayer

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import com.example.navaplayer.data.repository.AudioRepository
import com.example.navaplayer.player.PlayerController
import com.example.navaplayer.ui.screens.home.HomeViewModel
import com.example.navaplayer.ui.screens.player.PlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Player
    single { ExoPlayer.Builder(get<Application>()).build() }

    // Repository (چون state نداره می‌تونه factory باشه یا single)
    factory { AudioRepository(androidContext()) }

    // ViewModel
    viewModel { HomeViewModel(get() , get ()) }

    single { PlayerController(androidContext()) }

    viewModel { PlayerViewModel(get()) }

}