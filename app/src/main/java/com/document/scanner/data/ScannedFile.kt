package com.document.scanner.data

import android.net.Uri

data class ScannedFile(
    var fileUri: Uri = Uri.parse(""),
    var contentUri: Uri = Uri.parse(""),
    var totalSize: Long = 0,
    var lastModifiedDate: Long = 0
)