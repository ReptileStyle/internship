package com.kuzevanov.filemanager.fileSystem.LocalFileSystem

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.kuzevanov.filemanager.fileSystem.FileSystem
import com.kuzevanov.filemanager.fileSystem.FileSystemEntry
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.HashcodeRepository
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.MD5
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.MediaFinderHelper
import com.kuzevanov.filemanager.fileSystem.model.Hashcode
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class LocalFileSystem @Inject constructor(
    @ApplicationContext
    val context: Context,
    val repository: HashcodeRepository
) : FileSystem() {
    init {
//        context.startService(Intent(context,FileObserverService::class.java))
        repository.startCheckingFiles()


    }

    override fun getEntry(path: String) =
        LocalFileSystemEntry(this, File(path), this::checkIfChildrenModified)

    fun getAllByType(type: SpecialFolderTypes) =
        MediaFinderHelper(context.contentResolver).getAllByType(type)

    override fun load() = Unit

    suspend fun checkIfChildrenModified(path: String): Map<String, Boolean> {
        val map = mutableMapOf<String, Boolean>()
        repository.getAllHashcodesInDir(path).forEach {
            map[it.path] = it.isChanged
        }
        Log.d("files4",map.toString())
        return map
    }
    fun shareFiles(files: List<FileSystemEntry>){
        try {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            val uriList:ArrayList<Uri> = arrayListOf()
            files.forEach {
                uriList.add(FileProvider.getUriForFile(context,"${context.packageName}.provider",File(it.path)))
            }
            intent.setType("*/*")
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing file...")
            intent.putExtra(Intent.EXTRA_TEXT, "Sharing file...")
            context.startActivity(Intent.createChooser(intent, "Share file").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }catch (e:Exception){
            throw e
        }
    }
}

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