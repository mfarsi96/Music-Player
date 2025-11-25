package com.example.navaplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.navaplayer.data.model.Audio

class AudioRepository(private val context: Context) {

    fun getAudioFiles(): List<Audio> {
        val audioList = mutableListOf<Audio>()

        // ستون‌هایی که از دیتابیس گوشی می‌خوایم
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC
        )

        // شرط جستجو: فقط فایل‌هایی که موزیک هستند
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC" // مرتب‌سازی بر اساس نام
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val duration = it.getInt(durationColumn)

                // ساخت URI قابل پخش برای هر فایل
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                audioList.add(Audio(id, contentUri, title, artist, duration))
            }
        }
        return audioList
    }
}