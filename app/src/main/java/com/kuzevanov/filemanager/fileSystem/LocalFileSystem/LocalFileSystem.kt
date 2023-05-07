package com.kuzevanov.filemanager.fileSystem.LocalFileSystem

import android.content.Context
import android.util.Log
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
}

class LocalFileSystemEntry(
    override val fileSystem: FileSystem,
    private val javaFile: File,
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



    override fun readBytes(): ByteArray = javaFile.readBytes()
    override fun writeBytes(data: ByteArray): Boolean {
        javaFile.writeBytes(data)
        return true
    }

    override fun delete(recursive: Boolean): Boolean =
        if (recursive) javaFile.deleteRecursively() else javaFile.delete()

    override fun listFiles(): List<LocalFileSystemEntry>? {
        return javaFile.listFiles()?.map { LocalFileSystemEntry(fileSystem, it,checkIfChildrenModified) }
    }

    override fun getParent(): LocalFileSystemEntry? {
        return LocalFileSystemEntry(fileSystem, javaFile.parentFile ?: return null,checkIfChildrenModified)
    }

//    override suspend fun isModified(): Boolean {
//        getIsModifiedMapJob.join()
//        if(javaFile.name=="1.doc"){
//            Log.d("files3",isModifiedMap.toString())
//        }
//        return isModifiedMap[path] ?: false
//    }

    override fun getHashcode(): Hashcode {
        return Hashcode(path = path, hashcode = MD5.calculateMD5(javaFile))
    }


}