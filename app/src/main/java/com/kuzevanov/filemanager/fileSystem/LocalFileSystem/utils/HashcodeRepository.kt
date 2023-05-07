package com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils

import android.os.Environment
import com.kuzevanov.filemanager.fileSystem.hashDatabase.HashcodeDAO
import com.kuzevanov.filemanager.fileSystem.model.Hashcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class HashcodeRepository @Inject constructor(private val hashcodeDAO: HashcodeDAO) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    suspend fun insertHashcode(hashcode: Hashcode) = hashcodeDAO.insertHashcode(hashcode = hashcode)
    suspend fun updateHashcode(hashcode: Hashcode) = hashcodeDAO.updateHashcode(hashcode = hashcode)
    suspend fun deleteHashcode(hashcode: Hashcode) = hashcodeDAO.deleteHashcode(hashcode = hashcode)
    suspend fun getHashcode(path: String) = hashcodeDAO.getHashcode(path)
    suspend fun getAllHashcodesInDir(path: String) = hashcodeDAO.getAllHashcodesInDir(path)
    fun startCheckingFiles() {
        coroutineScope.launch {
            var rootFolder = Environment.getExternalStorageDirectory()
            fun getHashesOfAllChildren(file: File) {
                val listFiles = file.listFiles()
                if(listFiles!=null) {
//                    Log.d("files", "hashing ${file.absoluteFile}")
                    coroutineScope.launch {
                        val storedHashForChildren = getAllHashcodesInDir(file.absolutePath)
//                        storedHashForChildren.forEach {
//                            Log.d("stored",it.toString())
//                        }
                        listFiles.forEach { file ->
                            if (!file.isDirectory) {
                                val hash = MD5.calculateMD5(file)
                                val storedHash =
                                    storedHashForChildren.find { it.path == file.absolutePath }
//                                if(file.name=="1.doc"){
//                                    Log.d("1.doc","$hash\n$storedHash")
//                                }
                                if (storedHash != null) {
//                                    if(storedHash.hashcode!=hash){
//                                        Log.d("files","hash not equal ${file.absoluteFile}")
//                                    }
                                    updateHashcode(
                                        hashcode = Hashcode(
                                            file.absolutePath,
                                            hash,
                                            storedHash.hashcode!=hash
                                        )
                                    )
                                } else {
                                    insertHashcode(hashcode = Hashcode(file.absolutePath, hash, depth = file.absolutePath.count{it=='/'}))
                                }
                            }
                        }
                    }
                    listFiles.forEach {
                        getHashesOfAllChildren(it)
                    }
                }

            }
            getHashesOfAllChildren(rootFolder)
        }
    }
}

