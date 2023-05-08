package com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils

import android.content.Context

import android.os.Build
import android.os.Environment
import android.util.Log
import com.kuzevanov.filemanager.fileSystem.hashDatabase.HashcodeDAO
import com.kuzevanov.filemanager.fileSystem.hashDatabase.RecentFileDAO
import com.kuzevanov.filemanager.fileSystem.model.Hashcode
import com.kuzevanov.filemanager.fileSystem.model.RecentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

class HashcodeRepository @Inject constructor(
    @ApplicationContext
    context:Context,
    private val hashcodeDAO: HashcodeDAO,
    private val recentFileDAO: RecentFileDAO) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val installTime = context.packageManager.getPackageInfo("com.kuzevanov.filemanager",0).lastUpdateTime

    suspend fun insertHashcode(hashcode: Hashcode) = hashcodeDAO.insertHashcode(hashcode = hashcode)
    suspend fun updateHashcode(hashcode: Hashcode) = hashcodeDAO.updateHashcode(hashcode = hashcode)
    suspend fun deleteHashcode(hashcode: Hashcode) = hashcodeDAO.deleteHashcode(hashcode = hashcode)
    suspend fun getHashcode(path: String) = hashcodeDAO.getHashcode(path)
    suspend fun getAllHashcodesInDir(path: String) = hashcodeDAO.getAllHashcodesInDir(path)

    suspend fun insertRecent(recentFile: RecentFile) = recentFileDAO.insertRecent(recentFile)

    /**
     * optimization:
     * 1) check most important folders and add new/changed files to RecentFile list
     * 1) check everything not in android or cache dirs and add new/changed files to RecentFile list
     * 2) check cache dirs, do not add to recent files
     * 3) check android dir
     */
    fun startCheckingFiles() {
        val rootFolder = Environment.getExternalStorageDirectory()
        val job0 = coroutineScope.launch {
            importantDirs.forEach {
                val superJob = Job()
                val file = Environment.getExternalStoragePublicDirectory(it)
                getHashesOfAllChildren(
                    file,
                    conditionToSkip = { false },
                    job = superJob,
                    shouldAddToRecent = true
                )
                superJob.children.forEach { it.join() }
            }
        }

        val job1 = coroutineScope.launch {
            val superJob = Job()
            job0.join()
            getHashesOfAllChildren(
                rootFolder,
                conditionToSkip = { file ->
                    file.isHidden || file.absolutePath == "${Environment.getExternalStorageDirectory()}/Android" || file.absolutePath.contains(
                        "cache"
                    )
                },
                job = superJob,
                shouldAddToRecent = false
            )
            superJob.children.forEach { it.join() }
        }
        val job2 = coroutineScope.launch {
            job1.join()
            val superJob = Job()
            getHashesOfAllChildren(
                rootFolder,
                conditionToSkip = { file ->
                    file.isHidden || file.absolutePath == "${Environment.getExternalStorageDirectory()}/Android" || !file.absolutePath.contains(
                        "cache"
                    )

                },
                job = superJob,
                shouldAddToRecent = false
            )
            superJob.children.forEach { it.join() }
        }
        val job3 = coroutineScope.launch {
            job2.join()
            val superJob = Job()
            getHashesOfAllChildren(
                File("${rootFolder.absolutePath}/Android"), { file ->
                    file.isHidden
                },
                job = superJob,
                shouldAddToRecent = false
            )
            superJob.children.forEach { it.join() }
        }
    }


    private val importantDirs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
//                "${Environment.getExternalStorageDirectory().absolutePath}/Download",
                Environment.DIRECTORY_DOWNLOADS,
                Environment.DIRECTORY_PICTURES,
                Environment.DIRECTORY_DCIM,
                Environment.DIRECTORY_SCREENSHOTS,
                Environment.DIRECTORY_DOCUMENTS,
                Environment.DIRECTORY_MUSIC,
                Environment.DIRECTORY_ALARMS,
                Environment.DIRECTORY_AUDIOBOOKS,
                Environment.DIRECTORY_NOTIFICATIONS,
                Environment.DIRECTORY_RECORDINGS,
                Environment.DIRECTORY_PODCASTS,
                Environment.DIRECTORY_RINGTONES,
                Environment.DIRECTORY_MOVIES
            )
        } else {
            listOf(
//                "${Environment.getExternalStorageDirectory().absolutePath}/Download",
                Environment.DIRECTORY_DOWNLOADS,
                Environment.DIRECTORY_PICTURES,
                Environment.DIRECTORY_DCIM,
                Environment.DIRECTORY_SCREENSHOTS,
                Environment.DIRECTORY_DOCUMENTS,
                Environment.DIRECTORY_MUSIC,
                Environment.DIRECTORY_ALARMS,
                Environment.DIRECTORY_AUDIOBOOKS,
                Environment.DIRECTORY_NOTIFICATIONS,
                Environment.DIRECTORY_PODCASTS,
                Environment.DIRECTORY_RINGTONES,
                Environment.DIRECTORY_MOVIES
            )
        }
    } else {
        listOf(
//            "${Environment.getExternalStorageDirectory().absolutePath}/Download",//for some reason in is not DIRECTORY_DOWNLOADS
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_ALARMS,
            Environment.DIRECTORY_NOTIFICATIONS,
            Environment.DIRECTORY_PODCASTS,
            Environment.DIRECTORY_RINGTONES,
            Environment.DIRECTORY_MOVIES
        )
    }

    private fun getHashesOfAllChildren(file: File, conditionToSkip: (File) -> Boolean,job:Job,shouldAddToRecent:Boolean) {
        if (conditionToSkip(file)) return
        val listFiles = file.listFiles()
        if (listFiles != null) {
            coroutineScope.launch(job) {
                val storedHashForChildren = getAllHashcodesInDir(file.absolutePath)
                listFiles.forEach { file ->
                    coroutineScope.launch(job) {
                        if (!file.isDirectory && !file.isHidden) {
                            val hash = MD5.calculateMD5(file)
                            val storedHash =
                                storedHashForChildren.find { it.path == file.absolutePath }
                            if (storedHash != null) {
                                if(storedHash.hashcode==hash){
                                    if(storedHash.isChanged){
                                        updateHashcode(
                                            hashcode = Hashcode(
                                                file.absolutePath,
                                                hash,
                                                false
                                            )
                                        )
                                    }
                                }else{
                                    updateHashcode(
                                        hashcode = Hashcode(
                                            file.absolutePath,
                                            hash,
                                            true
                                        )
                                    )
                                    if(shouldAddToRecent){
                                        Log.d("files",file.absolutePath)
                                        insertRecent(RecentFile(path = file.absolutePath,date = System.currentTimeMillis()))
                                    }
                                }

                            } else {
                                insertHashcode(
                                    hashcode = Hashcode(
                                        file.absolutePath,
                                        hash,
                                        depth = file.absolutePath.count { it == '/' })
                                )
                                if(shouldAddToRecent && file.lastModified()>installTime){
                                    insertRecent(RecentFile(path = file.absolutePath,date = System.currentTimeMillis()))
                                }
                            }
                        }
//                        Log.d("files", file.absolutePath)
                    }
                }
            }
            listFiles.forEach {
                getHashesOfAllChildren(it, conditionToSkip,job,shouldAddToRecent)
            }
        }
    }
}



