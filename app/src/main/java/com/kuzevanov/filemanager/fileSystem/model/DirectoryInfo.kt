package com.kuzevanov.filemanager.fileSystem.model


import com.kuzevanov.filemanager.fileSystem.FileSystemEntry
import java.io.IOException


data class DirectoryInfo(val fileSystemEntry: FileSystemEntry) {
    val files = fileSystemEntry.listFiles()?.map { DirectoryEntry(it, directoryInfo = this) } ?: throw IOException("Unable to read files")
    val path = fileSystemEntry.path
    val name = if(getParent()?.path=="/storage/emulated") "Root" else fileSystemEntry.name
    val fileCount
        get() = files.size
    fun clone(newPath: String = path) = DirectoryInfo(fileSystemEntry.fileSystem.getEntry(newPath))
    fun getParent(): DirectoryEntry? {
        return DirectoryEntry(fileSystemEntry.getParent() ?: return null, "..", directoryInfo = this)
    }



}
