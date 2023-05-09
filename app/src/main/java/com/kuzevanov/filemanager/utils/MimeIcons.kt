package com.kuzevanov.filemanager.utils

import android.webkit.MimeTypeMap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

import com.kuzevanov.filemanager.R
import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import java.io.File

@Composable
fun DirectoryEntry.iconInfo(): Pair<ImageVector, String> {
    val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)
    return if (this.isDirectory) { // Folders
        Icons.Filled.Folder to "Folder"
    } else { // Otherwise, check the mime type
        mimeTypeIconMap[mime]
            ?: mimeTypeCustomIconMap[mime].let{
                if(it!=null){
                    Pair(ImageVector.vectorResource(id = it.first),it.second)
                }else{
                    null
                }
            }
            ?: getGeneralContentIcon(mime)
            ?: (Icons.Filled.QuestionMark to "Unknown") // If an unrecognized mime type, resort to "Unknown"
    }
}

@Composable
fun File.iconInfo(): Pair<ImageVector, String> {
    val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)
    return if (this.isDirectory) { // Folders
        Icons.Filled.Folder to "Folder"
    } else { // Otherwise, check the mime type
        mimeTypeIconMap[mime]
            ?: mimeTypeCustomIconMap[mime].let{
                if(it!=null){
                    Pair(ImageVector.vectorResource(id = it.first),it.second)
                }else{
                    null
                }
            }
            ?: getGeneralContentIcon(mime)
            ?: (Icons.Filled.QuestionMark to "Unknown") // If an unrecognized mime type, resort to "Unknown"
    }
}

/**
 * A map of mime types to their respective icons and descriptions
 */
private val mimeTypeIconMap = mapOf(
    // Archive formats
    "application/gzip" to (Icons.Filled.FolderZip to "Archive"),
    "application/x-7z-compressed" to (Icons.Filled.FolderZip to "Archive"),
    "application/zip" to (Icons.Filled.FolderZip to "Archive"),
    "application/java-archive" to (Icons.Filled.FolderZip to "Archive"),
    "application/vnd.android.package-archive" to (Icons.Filled.Android to "Archive"),
    "application/rar" to (Icons.Filled.Android to "Archive"),
    // Documents
    "application/epub+zip" to (Icons.Filled.Book to "Epub Ebook"),
    "application/x-mobipocket-ebook" to (Icons.Filled.Book to "Mobi Ebook"),
    "application/pdf" to (Icons.Filled.PictureAsPdf to "Pdf"),
    "application/vnd.openxmlformats-officedocument.presentationml.presentation" to (Icons.Filled.TextSnippet to "Word document"),
    // Audio
    "audio/mpeg" to (Icons.Filled.AudioFile to "MP3 Audio"),
    "audio/x-wav" to (Icons.Filled.AudioFile to "WAV Audio"),
    // Text formats
    "text/plain" to (Icons.Filled.TextSnippet to "Text"),
    "text/x-java" to (Icons.Filled.Code to "Java source code"),
    "text/html" to (Icons.Filled.Html to "HTML"),
    "text/css" to (Icons.Filled.Css to "CSS"),
    "text/javascript" to (Icons.Filled.Javascript to "Javascript"),
    "application/json" to (Icons.Filled.Code to "JSON"),
    "application/ld+json" to (Icons.Filled.Code to "JSON+LD"),
    //word
    "application/msword" to (Icons.Filled.Description to "Word document"),
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to (Icons.Filled.Description to "Word document"),
    "application/vnd.openxmlformats-officedocument.wordprocessingml.template" to (Icons.Filled.Description to "Word document"),
    "application/vnd.ms-word.document.macroEnabled.12" to (Icons.Filled.Description to "Word document"),
    "application/vnd.ms-word.template.macroEnabled.12" to (Icons.Filled.Description to "Word document"),
    //excel
    "application/vnd.ms-excel" to (Icons.Filled.TableView  to "excel document"),
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" to (Icons.Filled.TableView  to "excel document"),
    "application/vnd.openxmlformats-officedocument.spreadsheetml.template" to (Icons.Filled.TableView  to "excel document"),
    "application/vnd.ms-excel.sheet.macroEnabled.12" to (Icons.Filled.TableView  to "excel document"),
    "application/vnd.ms-excel.template.macroEnabled.12" to (Icons.Filled.TableView  to "excel document"),
    "application/vnd.ms-excel.addin.macroEnabled.12" to (Icons.Filled.TableView  to "excel document"),
    "application/vnd.ms-excel.sheet.binary.macroEnabled.12" to (Icons.Filled.TableView  to "excel document"),
    //images


    // Other
    "application/pgp-keys" to (Icons.Filled.Key to "PGP Keys"),
    "application/x-msdos-program" to (Icons.Filled.DesktopWindows to "Windows exe"),
    "application/x-apple-diskimage" to (Icons.Filled.InstallDesktop to "Mac installer")
)

private val mimeTypeCustomIconMap = mapOf( //icon is too big
    "application/vnd.ms-excel" to (R.drawable.excel_file_icon  to "excel document"),
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" to (R.drawable.excel_file_icon  to "excel document"),
    "application/vnd.openxmlformats-officedocument.spreadsheetml.template" to (R.drawable.excel_file_icon  to "excel document"),
    "application/vnd.ms-excel.sheet.macroEnabled.12" to (R.drawable.excel_file_icon  to "excel document"),
    "application/vnd.ms-excel.template.macroEnabled.12" to (R.drawable.excel_file_icon  to "excel document"),
    "application/vnd.ms-excel.addin.macroEnabled.12" to (R.drawable.excel_file_icon  to "excel document"),
    "application/vnd.ms-excel.sheet.binary.macroEnabled.12" to (R.drawable.excel_file_icon  to "excel document"),
)

private fun getGeneralContentIcon(mime:String?):Pair<ImageVector,String>?{
    if(mime==null) return null
    return when(mime.substringBefore('/')){
        "image"-> Icons.Filled.Image to "Image file"
        "audio"-> Icons.Filled.AudioFile to "Audio file"
        "video"-> Icons.Filled.VideoFile to "Video file"
        else-> null
    }
}