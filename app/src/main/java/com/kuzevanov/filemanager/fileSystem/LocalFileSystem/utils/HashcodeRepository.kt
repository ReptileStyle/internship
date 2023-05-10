package com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils

import android.content.Context
import android.content.SharedPreferences

import android.os.Build
import android.os.Environment
import android.util.Log
import com.kuzevanov.filemanager.fileSystem.hashDatabase.HashcodeDAO
import com.kuzevanov.filemanager.fileSystem.hashDatabase.RecentFileDAO
import com.kuzevanov.filemanager.domain.fileSystem.model.Hashcode
import com.kuzevanov.filemanager.domain.fileSystem.model.RecentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class HashcodeRepository @Inject constructor(
    @ApplicationContext
    context: Context,
    private val hashcodeDAO: HashcodeDAO,
    private val recentFileDAO: RecentFileDAO,
    private val sharedPreferences: SharedPreferences
) {
    //dont want to use too much power, sore limited_parellelism=cores/2
    @OptIn(ExperimentalCoroutinesApi::class)
    val coroutineScope = CoroutineScope(
        Dispatchers.IO.limitedParallelism(
            Runtime.getRuntime().availableProcessors() / 2
        )
    )

    val installTime =
        context.packageManager.getPackageInfo("com.kuzevanov.filemanager", 0).lastUpdateTime

    suspend fun insertHashcode(hashcode: Hashcode) = hashcodeDAO.insertHashcode(hashcode = hashcode)
    suspend fun updateHashcode(hashcode: Hashcode) = hashcodeDAO.updateHashcode(hashcode = hashcode)
    suspend fun deleteHashcode(hashcode: Hashcode) = hashcodeDAO.deleteHashcode(hashcode = hashcode)
    suspend fun getHashcode(path: String) = hashcodeDAO.getHashcode(path)
    suspend fun getAllHashcodesInDir(path: String) = hashcodeDAO.getAllHashcodesInDir(path)

    suspend fun insertRecent(recentFile: RecentFile) = recentFileDAO.insertRecent(recentFile)


    private val currentLaunchID = sharedPreferences.getInt("currentLaunchId", 0) + 1

    init {
        sharedPreferences.edit().putInt("currentLaunchId", currentLaunchID).apply()
    }

    /**
     * this fun is suspend, so it uses different coroutineScope than this class coroutineScope.
     * because it must be cancelled if user leaves directory, also it should use more power to display
     * changes to uses ASAP, this coroutineScope is busy at launch, so not good idea to use it
     * returns flow of file paths, that have been changed, has no affect on database
     */
    suspend fun getChangedFileInDir(path: String, force: Boolean = false): Flow<String> {
        return flow {
            val file = File(path)
            val storedHashesForChildren = getAllHashcodesInDir(file.absolutePath)
            storedHashesForChildren.forEach { hashcode ->
                if (hashcode.hashingLaunchID == currentLaunchID) {
                    if (hashcode.isChanged) emit(hashcode.path)
                }
            }
            file.listFiles()?.sortedBy { -it.lastModified() }?.forEach { childFile ->
                if (!childFile.isDirectory) {
                    val storedHash =
                        storedHashesForChildren.firstOrNull { it.path == childFile.absolutePath }
                    if (storedHash != null) {
                        Log.d("files", "${storedHash.hashingLaunchID}, $currentLaunchID")
                        if (!force) {
                            if (storedHash.hashingLaunchID != currentLaunchID && !MD5.checkMD5(
                                    storedHash.hashcode,
                                    childFile
                                )
                            ) {
                                emit(childFile.absolutePath)
                                recentFileDAO.insertRecent(RecentFile(childFile.absolutePath,System.currentTimeMillis()))
                            } else {
                                if (storedHash.isChanged) {
                                    emit(childFile.absolutePath)
                                    recentFileDAO.insertRecent(RecentFile(childFile.absolutePath,System.currentTimeMillis()))
                                }
                            }
                        } else {
                            if (!MD5.checkMD5(storedHash.hashcode, childFile)) {
                                emit(childFile.absolutePath)
                                recentFileDAO.insertRecent(RecentFile(childFile.absolutePath,System.currentTimeMillis()))
                            }
                        }
                    } else {
                        if (childFile.lastModified() >= installTime) {
                            emit(childFile.absolutePath)
                            recentFileDAO.insertRecent(RecentFile(childFile.absolutePath,System.currentTimeMillis()))
                        }
                    }
                }
            }
        }
    }

    //    var mostImportantDirsJob: Job? = null
    var checkingAllFilesJob: Job = Job()

    /**
     * we pause main hashing, begin hashing in most important dirs, than resume main hashing.
     * anyway it is pretty slow, but good enough
     */
    suspend fun refreshMostImportantDirs() {
        checkingAllFilesJob.cancel()
        checkMostImportantDirs(true)
        startCheckingFiles()
    }

    suspend private fun checkMostImportantDirs(force: Boolean) {
        importantDirs.forEach {
            val superJob = Job()
            val file = Environment.getExternalStoragePublicDirectory(it)
            getHashesOfAllChildren(
                file,
                conditionToSkip = { false },
                job = superJob,
                shouldAddToRecent = true,
                force = force
            )
            superJob.children.forEach { it.join() }
        }
    }

    /**
     * optimization:
     * 1) check most important folders and add new/changed files to RecentFile list
     * 1) check everything not in android or cache dirs and add new/changed files to RecentFile list
     * 2) check cache dirs, do not add to recent files
     * 3) check android dir
     */

    fun startCheckingFiles() {
        Log.d("files", Runtime.getRuntime().availableProcessors().toString())
        val rootFolder = Environment.getExternalStorageDirectory()
        checkingAllFilesJob = CoroutineScope(Dispatchers.Default).launch {
            val job0 = coroutineScope.launch(checkingAllFilesJob) {
                checkMostImportantDirs(false)
            }
            val job1 = coroutineScope.launch(checkingAllFilesJob) {
                val superJob = Job()
                job0.join()
//            Log.d("files2","job0 joined")
                getHashesOfAllChildren(
                    rootFolder,
                    conditionToSkip = { file ->
                        file.isHidden || file.absolutePath == "${Environment.getExternalStorageDirectory()}/Android" || file.absolutePath.contains(
                            "cache"
                        )
                    },
                    job = superJob,
                    shouldAddToRecent = false,
                    force = false
                )
                superJob.children.forEach { it.join() }
            }
            val job2 = coroutineScope.launch(checkingAllFilesJob) {
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
                    shouldAddToRecent = false,
                    force = false
                )
                superJob.children.forEach { it.join() }
            }
            val job3 = coroutineScope.launch(checkingAllFilesJob) {
                job2.join()
                val superJob = Job()
                getHashesOfAllChildren(
                    File("${rootFolder.absolutePath}/Android"), { file ->
                        file.isHidden
                    },
                    job = superJob,
                    shouldAddToRecent = false,
                    force = false
                )
                superJob.children.forEach { it.join() }
            }
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

    private fun getHashesOfAllChildren(
        file: File,
        conditionToSkip: (File) -> Boolean,
        job: Job,
        shouldAddToRecent: Boolean,
        force: Boolean
    ) {
        if (conditionToSkip(file)) return
        val listFiles = file.listFiles()
        if (listFiles != null) {
            coroutineScope.launch(job) {
                val storedHashForChildren = getAllHashcodesInDir(file.absolutePath)
                listFiles.forEach { file ->
                    coroutineScope.launch(job) {
                        if (!file.isDirectory && !file.isHidden) {
                            val storedHash =
                                storedHashForChildren.find { it.path == file.absolutePath }
                            if (!force && storedHash?.hashingLaunchID == currentLaunchID) return@launch
                            val hash = MD5.calculateMD5(file)
                            if (storedHash != null) {
                                if (storedHash.hashcode == hash) {
                                    updateHashcode(
                                        hashcode = Hashcode(
                                            file.absolutePath,
                                            hash,
                                            false,
                                            hashingLaunchID = currentLaunchID
                                        )
                                    )
                                } else {
                                    updateHashcode(
                                        hashcode = Hashcode(
                                            file.absolutePath,
                                            hash,
                                            true,
                                            hashingLaunchID = currentLaunchID
                                        )
                                    )
                                    if (shouldAddToRecent) {
                                        insertRecent(
                                            RecentFile(
                                                path = file.absolutePath,
                                                date = System.currentTimeMillis()
                                            )
                                        )
                                    }
                                }

                            } else {
                                insertHashcode(
                                    hashcode = Hashcode(
                                        file.absolutePath,
                                        hash,
                                        depth = file.absolutePath.count { it == '/' },
                                        hashingLaunchID = currentLaunchID,
                                        isChanged = file.lastModified()>installTime
                                    ),
                                )
                                if (shouldAddToRecent && file.lastModified() > installTime) {
                                    insertRecent(
                                        RecentFile(
                                            path = file.absolutePath,
                                            date = System.currentTimeMillis()
                                        )
                                    )
                                }
                            }
                        }
//                        Log.d("files", file.absolutePath)
                    }
                }
            }
            listFiles.forEach {
                getHashesOfAllChildren(it, conditionToSkip, job, shouldAddToRecent, force)
            }
        }
    }
}



