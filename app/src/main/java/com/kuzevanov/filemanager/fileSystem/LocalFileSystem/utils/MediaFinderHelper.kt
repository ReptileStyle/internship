package com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils


import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import java.util.*
import kotlin.collections.ArrayList


class MediaFinderHelper(
    private val resolver: ContentResolver
) {

    fun getAllByType(
        type: SpecialFolderTypes
    ): ArrayList<String> {
        if(type is SpecialFolderTypes.Images){
            return ArrayList(getCameraImages())
        }
        val videoItemHashSet: HashSet<String> = HashSet()
        val projection = when (type) {
            SpecialFolderTypes.Videos -> arrayOf(
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME
            )
            SpecialFolderTypes.Images -> arrayOf(
                MediaStore.Images.ImageColumns.DATA
            )
            SpecialFolderTypes.Music -> arrayOf(
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME
            )
            SpecialFolderTypes.Downloads -> arrayOf(
                MediaStore.DownloadColumns.DATA,
                MediaStore.Downloads.DISPLAY_NAME
            )
            else -> {
                throw error("not implemented")
            }
        }
        val uri: Uri = when (type) {
            SpecialFolderTypes.Videos -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            SpecialFolderTypes.Images -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            SpecialFolderTypes.Music -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            SpecialFolderTypes.Downloads -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

    val CAMERA_IMAGE_BUCKET_NAME = (Environment.getExternalStorageDirectory().toString()
            + "/DCIM/Camera")
    val CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME)

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    fun getBucketId(path: String): String {
        return path.lowercase(Locale.getDefault()).hashCode().toString()
    }

    private fun getCameraImages(): List<String> {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"
        val selectionArgs = arrayOf(CAMERA_IMAGE_BUCKET_ID)
        val cursor: Cursor? = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
        if(cursor!=null) {
            val result = ArrayList<String>(cursor.count)
            if (cursor.moveToFirst()) {
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                do {
                    val data = cursor.getString(dataColumn)
                    result.add(data)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return result
        }else{
            throw Exception("cursor is null")
        }
    }
}