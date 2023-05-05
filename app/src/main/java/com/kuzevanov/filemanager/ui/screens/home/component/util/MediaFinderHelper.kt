package com.kuzevanov.filemanager.ui.screens.home.component.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri


class MediaFinderHelper(
    private val resolver: ContentResolver
) {

    fun getAllByType(
        type: Int
    ): ArrayList<String?> {
        val videoItemHashSet: HashSet<String> = HashSet()
        val projection = when (type) {
            SpecialFolders.Videos -> arrayOf(
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME
            )
            SpecialFolders.Images -> arrayOf(
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            SpecialFolders.Music -> arrayOf(
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME
            )
            SpecialFolders.Downloads -> arrayOf(
                MediaStore.DownloadColumns.DATA,
                MediaStore.Downloads.DISPLAY_NAME
            )
            else -> {
                throw error("not implemented")
            }
        }
        val uri: Uri = when (type) {
            SpecialFolders.Videos -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            SpecialFolders.Images -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            SpecialFolders.Music -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            SpecialFolders.Downloads -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toUri()
            }
            else -> {
                throw error("not implemented")
            }
        }

        val cursor: Cursor? = resolver
            .query(uri, projection, null, null, null)
        if (cursor != null) {
            try {
                cursor.moveToFirst()
                do {
                    videoItemHashSet.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                } while (cursor.moveToNext())
                cursor.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ArrayList(videoItemHashSet)
    }
}