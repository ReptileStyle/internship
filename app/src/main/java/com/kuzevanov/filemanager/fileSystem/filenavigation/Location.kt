package com.kuzevanov.filemanager.fileSystem.filenavigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@Parcelize
@Serializable
data class Location(val path: String) : Parcelable {
    val navUrl: String
        get() = "directory/${Json.encodeToString(this).urlEncode()}"
}

fun String.urlEncode(): String {
    return URLEncoder.encode(this, Charsets.UTF_8.toString())
}