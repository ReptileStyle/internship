package com.kuzevanov.filemanager.fileSystem.model

import android.webkit.MimeTypeMap
import com.kuzevanov.filemanager.fileSystem.FileSystemEntry

data class DirectoryEntry(val fileSystemEntry: FileSystemEntry, val name: String = fileSystemEntry.name,val directoryInfo: DirectoryInfo) {
    val extension = fileSystemEntry.extension
    val path = fileSystemEntry.path
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    val isDirectory = fileSystemEntry.isDirectory
    val lastModified = fileSystemEntry.lastModified
    val size = fileSystemEntry.size
    val countChildren = fileSystemEntry.countChildren
    fun delete(recursive: Boolean = false) = fileSystemEntry.delete(recursive)

    fun open() = fileSystemEntry.open()
}