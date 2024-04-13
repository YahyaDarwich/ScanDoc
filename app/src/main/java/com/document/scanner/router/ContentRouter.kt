package com.document.scanner.router

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.document.scanner.data.ScannedFile
import java.io.File

class ContentRouter(private val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sharedInstance: ContentRouter? = null

        @Synchronized
        fun getInstance(context: Context): ContentRouter {
            if (sharedInstance == null) {
                sharedInstance = ContentRouter(context)
            }

            return sharedInstance!!
        }
    }

    fun createScannedDirectories(dirType: String, dirName: String) {
        val dir = File(context.getExternalFilesDir(dirType), dirName)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    fun getScannedFiles(): MutableList<ScannedFile> {
        val listOfScannedUris = mutableListOf<ScannedFile>()
        if (((context.cacheDir.listFiles()?.size ?: 0) > 0)) {
            context.cacheDir.listFiles()?.map {
                if (it.path.endsWith("mlkit_docscan_ui_client")) {
                    File(it.path).listFiles()?.map { file ->
                        if (file.isFile) {
                            listOfScannedUris.add(
                                ScannedFile(
                                    Uri.parse(file.path),
                                    getContentUri(file),
                                    file.length(),
                                    file.lastModified()
                                )
                            )
                        }
                    } ?: emptyList()
                }
            }
        }

        return listOfScannedUris.sortedByDescending { it.lastModifiedDate }.toMutableList()
    }

    fun getContentUri(file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}