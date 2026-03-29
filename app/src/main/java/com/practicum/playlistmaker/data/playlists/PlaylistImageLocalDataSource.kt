package com.practicum.playlistmaker.data.playlists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistImageLocalDataSource(
    private val context: Context
) {

    suspend fun savePlaylistCover(uri: Uri, playlistId: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val coversDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers").also {
                if (!it.exists()) it.mkdirs()
            }

            val file = File(coversDir, "$playlistId.jpg")

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    BitmapFactory.decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                }
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}