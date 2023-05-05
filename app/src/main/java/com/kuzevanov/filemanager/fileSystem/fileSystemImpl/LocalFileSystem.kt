package com.kuzevanov.filemanager.fileSystem.fileSystemImpl

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.kuzevanov.filemanager.fileSystem.FileSystem
import com.kuzevanov.filemanager.fileSystem.FileSystemEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class LocalFileSystem @Inject constructor(
    @ApplicationContext
    val context:Context) : FileSystem() {
    override fun getEntry(path: String) = LocalFileSystemEntry(this, File(path))
    override fun load() = Unit
}

class LocalFileSystemEntry(override val fileSystem: FileSystem, private val javaFile: File) : FileSystemEntry() {
    override val path: String = javaFile.absolutePath
    override val name: String = javaFile.name
    override val extension: String = javaFile.extension
    override val lastModified: Long = javaFile.lastModified()
    override val isDirectory: Boolean = javaFile.isDirectory
    override fun readBytes(): ByteArray = javaFile.readBytes()
    override fun writeBytes(data: ByteArray): Boolean {
        javaFile.writeBytes(data)
        return true
    }

    override fun delete(recursive: Boolean): Boolean = if (recursive) javaFile.deleteRecursively() else javaFile.delete()

    override fun listFiles(): List<LocalFileSystemEntry>?{
//        val uri = MediaStore .Files.getContentUri(path)
//
//
//        Log.d("files",this.path)
//        Log.d("files",javaFile.exists().toString())
//        Log.d("files",javaFile.isDirectory.toString())
//        try {
//            javaFile.listFiles()
//        }catch (e:Exception){
//            Log.d("files",e.toString())
//        }
        return javaFile.listFiles()?.map { LocalFileSystemEntry(fileSystem, it) }
    }

    override fun getParent(): LocalFileSystemEntry? {
        return LocalFileSystemEntry(fileSystem, javaFile.parentFile ?: return null)
    }


}