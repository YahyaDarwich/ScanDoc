package com.document.scanner.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.document.scanner.R
import com.document.scanner.data.ScannedFile
import com.document.scanner.data.SearchFilter
import com.document.scanner.router.ContentRouter
import com.document.scanner.ui.Dialogs.CustomDialog
import com.document.scanner.ui.components.FileCell
import com.document.scanner.ui.components.LottieEmptyList
import com.document.scanner.ui.components.SearchBarWithFilterChips
import java.io.File

@Composable
fun ScannedFilesScreen(
    scannedFiles: MutableList<ScannedFile>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val scannedFilesState = remember { mutableStateOf(scannedFiles) }
    var showDialog by remember { mutableStateOf(false) }
    var fileUriToEdit by remember { mutableStateOf(Uri.parse("")) }
    var currentSelectedFiler by remember { mutableStateOf(SearchFilter.ALL) }
    var currentSearchText by remember { mutableStateOf("") }

    var filterElements: kotlin.Array<String>? = null
    if (currentSelectedFiler == SearchFilter.PDF) filterElements = arrayOf("pdf")
    else if (currentSelectedFiler == SearchFilter.IMAGES) filterElements =
        arrayOf("jpg", "png", "jpeg")

    scannedFilesState.value =
        scannedFiles.filter { scannedFile ->
            scannedFile.fileUri.lastPathSegment?.let {
                filterElements?.contains(File(it).extension) ?: true && it.contains(
                    currentSearchText, ignoreCase = true
                )
            } ?: false
        }.toMutableList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            SearchBarWithFilterChips(
                modifier = Modifier
                    .fillMaxWidth(),
                filters = SearchFilter.values().toList(),
                onSelectFilter = {
                    currentSelectedFiler = it
                },
                currentSelectedFilter = currentSelectedFiler,
                onSearch = { currentSearchText = it }
            )

            if (scannedFilesState.value.isEmpty()) {
                LottieEmptyList()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(items = scannedFilesState.value) { index, item ->
                        val isPdf = item.fileUri.lastPathSegment?.endsWith("pdf") == true

                        FileCell(data = item,
                            onItemClick = {
                                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(
                                        item.contentUri,
                                        if (isPdf) "application/pdf" else "image/*"
                                    )
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }

                                if (viewIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(viewIntent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(if (isPdf) R.string.no_pdf_app else R.string.no_gallery_app),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onEdit = { uri ->
                                fileUriToEdit = uri
                                showDialog = true
                            }, onDelete = { uri ->
                                uri.path?.let { path ->
                                    if (File(path).exists()) {
                                        if (File(path).delete()) {
                                            var indexToDelete: Int = -1

                                            scannedFiles.forEachIndexed { index, scannedFile ->
                                                if (scannedFile.fileUri == uri) {
                                                    indexToDelete = index
                                                }
                                            }

                                            if (indexToDelete > -1) {
                                                scannedFiles.removeAt(indexToDelete)
                                            }

                                            Toast.makeText(
                                                context,
                                                if (isPdf) context.getString(R.string.pdf_deleted_successfully) else context.getString(
                                                    R.string.image_deleted_successfully
                                                ),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.something_wrong),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            if (isPdf) context.getString(R.string.pdf_not_exist) else context.getString(
                                                R.string.image_not_exist
                                            ),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onShareClick = {
                                val shareIntent =
                                    Intent(Intent.ACTION_SEND).apply {
                                        putExtra(Intent.EXTRA_STREAM, item.contentUri)
                                        type = if (isPdf) "application/pdf" else "image/*"
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }

                                if (shareIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(shareIntent)
                                }
                            }
                        )
                    }
                }
            }
        }

        CustomDialog(
            showDialog = showDialog,
            dialogTitle = stringResource(id = R.string.edit_dialog_title),
            textFieldLabel = stringResource(id = R.string.edit_dialog_field_label),
            onConfirm = { newName ->
                if (newName.isNotEmpty()) {
                    fileUriToEdit.lastPathSegment?.let {
                        if (it.contains(".") && !it.substringBefore(".")
                                .contentEquals(newName.trim(), ignoreCase = true)
                        ) {
                            fileUriToEdit.path?.let { path ->
                                val oldFile = File(path)
                                val newFile =
                                    File("${oldFile.parent}/${newName.trim()}.${oldFile.extension}")
                                if (oldFile.exists()) {
                                    if (oldFile.renameTo(newFile)) {
                                        val newScannedFile = ScannedFile(
                                            newFile.toUri(),
                                            contentUri = ContentRouter.getInstance(context)
                                                .getContentUri(newFile),
                                            lastModifiedDate = newFile.lastModified(),
                                            totalSize = newFile.length()
                                        )

                                        scannedFiles.forEachIndexed { index, scannedFile ->
                                            if (scannedFile.fileUri == fileUriToEdit) {
                                                scannedFiles[index] = newScannedFile
                                            }
                                        }

                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.name_edited_successfully),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.something_wrong),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.file_not_exist),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else Toast.makeText(
                            context,
                            context.getString(R.string.file_already_exist),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}