package com.kuzevanov.filemanager.fileSystem.LocalFileSystem

import android.content.ActivityNotFoundException
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.kuzevanov.filemanager.fileSystem.FileSystemEntry
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.MD5
import com.kuzevanov.filemanager.fileSystem.model.Hashcode
import java.io.File

class LocalFileSystemEntry(
    override val fileSystem: LocalFileSystem,
    val javaFile: File,
    override val checkIfChildrenModified: suspend (String) -> Map<String, Boolean>,
) :
    FileSystemEntry() {
    override val path: String = javaFile.absolutePath
    override val name: String = javaFile.name
    override val extension: String = javaFile.extension
    override val lastModified: Long = javaFile.lastModified()
    override val isDirectory: Boolean = javaFile.isDirectory
    override val size: Long = javaFile.length()
    override val countChildren: Int
        get() =
            if (isDirectory)
                javaFile.listFiles()?.size ?: 0
            else
                0
    override val isHidden: Boolean = javaFile.isHidden


    override fun readBytes(): ByteArray = javaFile.readBytes()
    override fun writeBytes(data: ByteArray): Boolean {
        javaFile.writeBytes(data)
        return true
    }

    override fun delete(recursive: Boolean): Boolean =
        if (recursive) javaFile.deleteRecursively() else javaFile.delete()

    override fun listFiles(): List<LocalFileSystemEntry>? {
        return javaFile.listFiles()
            ?.map { LocalFileSystemEntry(fileSystem, it, checkIfChildrenModified) }
    }

    override fun getParent(): LocalFileSystemEntry? {
        return LocalFileSystemEntry(
            fileSystem,
            javaFile.parentFile ?: return null,
            checkIfChildrenModified
        )
    }

    override fun getHashcode(): Hashcode {
        return Hashcode(path = path, hashcode = MD5.calculateMD5(javaFile))
    }

    override fun open() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            val uri = FileProvider.getUriForFile(
                fileSystem.context,
                "${fileSystem.context.packageName}.provider",
                javaFile
            )
            intent.setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileSystem.context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            throw e
        }
    }
}