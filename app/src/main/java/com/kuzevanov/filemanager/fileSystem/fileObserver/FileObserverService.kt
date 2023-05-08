package com.kuzevanov.filemanager.fileSystem.fileObserver

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder

class FileObserverService: Service() {
    private val fileObserver:RecursiveFileObserver = RecursiveFileObserver(Environment.getExternalStorageDirectory().absolutePath)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fileObserver.startWatching()
    }

    override fun onDestroy() {
        fileObserver.stopWatching()
        super.onDestroy()
    }
}