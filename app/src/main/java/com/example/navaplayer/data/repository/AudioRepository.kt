package com.example.navaplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.navaplayer.data.model.Audio

class AudioRepository(private val context: Context) {

    private val albumArtUri = Uri.parse("content://media/external/audio/albumart")
    fun getAudioFiles(): List<Audio> {
        val audioList = mutableListOf<Audio>()

        // ستون‌هایی که از دیتابیس گوشی می‌خوایم
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID
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
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val duration = it.getInt(durationColumn).toLong()
                val albumId = it.getLong(albumIdColumn)
                // ساخت URI قابل پخش برای هر فایل
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val coverUri = ContentUris.withAppendedId(albumArtUri, albumId)

                audioList.add(Audio(id, contentUri, title, artist, duration,coverUri))
            }
        }
        return audioList
    }
}