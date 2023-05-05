package com.kuzevanov.filemanager.fileSystem

abstract class FileSystem {
    var loaded: Boolean = false
    open fun load() {
        loaded = true
    }
    open fun unload() {
        loaded = false
    }
    abstract fun getEntry(path: String): FileSystemEntry
}
