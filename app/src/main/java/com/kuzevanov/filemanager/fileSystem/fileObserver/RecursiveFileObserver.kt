package com.kuzevanov.filemanager.fileSystem.fileObserver

import android.os.FileObserver
import android.util.Log
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class RecursiveFileObserver @JvmOverloads constructor(
    var mPath: String,
    var mMask: Int = ALL_EVENTS
) :
    FileObserver(mPath, mMask) {
    var mObservers: MutableList<SingleFileObserver>? = null
    override fun startWatching() {
        if (mObservers != null) return
        mObservers = ArrayList()
        val stack: Stack<String> = Stack<String>()
        stack.push(mPath)
        while (!stack.empty()) {
            val parent: String = stack.pop()
            mObservers!!.add(SingleFileObserver(parent, mMask))
            val path = File(parent)
            val files: Array<File> = path.listFiles() ?: continue
            for (i in files.indices) {
                if (files[i].isDirectory && !files[i].name.equals(".")
                    && !files[i].name.equals("..") && !files[i].isHidden //do not want to observe cache folders or something like that
                ) {
                    stack.push(files[i].path)
                }
            }
        }
        for (i in mObservers!!.indices) mObservers!![i].startWatching()
    }

    override fun stopWatching() {
        if (mObservers == null) return
        for (i in mObservers!!.indices) mObservers!![i].stopWatching()
        mObservers!!.clear()
        mObservers = null
    }

    override fun onEvent(event: Int, path: String?) {
        Log.d("fileobserver","$path :: $event")
    }
    inner class SingleFileObserver(private val mPath: String, mask: Int) :
        FileObserver(mPath, mask) {
        override fun onEvent(event: Int, path: String?) {
            val newPath = "$mPath/$path"
            this@RecursiveFileObserver.onEvent(event, newPath)
        }
    }

    companion object {
        var CHANGES_ONLY = CLOSE_WRITE or MOVE_SELF or MOVED_FROM
    }
}