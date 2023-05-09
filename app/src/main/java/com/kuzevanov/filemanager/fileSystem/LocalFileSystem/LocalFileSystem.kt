package com.kuzevanov.filemanager.fileSystem.LocalFileSystem
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.kuzevanov.filemanager.fileSystem.FileSystem
import com.kuzevanov.filemanager.fileSystem.FileSystemEntry
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.HashcodeRepository
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.MediaFinderHelper
import com.kuzevanov.filemanager.fileSystem.hashDatabase.RecentFileDAO
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class LocalFileSystem @Inject constructor(
    @ApplicationContext
    val context: Context,
    val repository: HashcodeRepository,
    val recentFileDAO: RecentFileDAO
) : FileSystem() {
    init {
//        context.startService(Intent(context,FileObserverService::class.java))
        repository.startCheckingFiles()
    }

    override fun getEntry(path: String) =
        LocalFileSystemEntry(this, File(path), this::checkIfChildrenModified)

    fun getAllByType(type: SpecialFolderTypes) =
        MediaFinderHelper(context.contentResolver).getAllByType(type)

    override fun load() = Unit

    suspend fun checkIfChildrenModified(path: String): Map<String, Boolean> {
        val map = mutableMapOf<String, Boolean>()
        repository.getAllHashcodesInDir(path).forEach {
            map[it.path] = it.isChanged
        }
        Log.d("files4",map.toString())
        return map
    }
    fun shareFiles(files: List<FileSystemEntry>){
        try {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            val uriList:ArrayList<Uri> = arrayListOf()
            val mimeTypeSet:MutableSet<String> = mutableSetOf()
            files.forEach {
                uriList.add(FileProvider.getUriForFile(context,"${context.packageName}.provider",File(it.path)))
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.extension)?.let{mimeTypeSet.add(it)}
            }
//            Log.d("asd",mimeTypeSet.joinToString("|"))
////            throw Exception(mimeTypeSet.joinToString("|"))
            intent.setType(mimeTypeSet.joinToString("|"))
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing file...")
            intent.putExtra(Intent.EXTRA_TEXT, "Sharing file...")
            context.startActivity(Intent.createChooser(intent, "Share file").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }catch (e:Exception){
            throw e
        }
    }

    fun getRecentFiles() = recentFileDAO.getAllRecentFlow()
    fun dropOutdatedRecentFiles() = recentFileDAO.deleteOutdated()

    suspend fun getChangedFileInDir(path:String) = repository.getChangedFileInDir(path)
}

