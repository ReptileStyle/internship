package com.kuzevanov.filemanager.fileSystem

import com.kuzevanov.filemanager.fileSystem.model.Hashcode

abstract class FileSystemEntry {
    abstract val fileSystem: FileSystem
    abstract val path: String
    abstract val name: String
    abstract val extension: String
    abstract val lastModified: Long
    abstract val isDirectory: Boolean
    abstract val size:Long
    abstract val countChildren:Int
    abstract val isModified:Boolean

    abstract fun readBytes(): ByteArray?
    abstract fun writeBytes(data: ByteArray): Boolean
    abstract fun delete(recursive: Boolean = false): Boolean
    abstract fun listFiles(): List<FileSystemEntry>?
    abstract fun getParent(): FileSystemEntry?
    abstract fun getHashcode():Hashcode

}