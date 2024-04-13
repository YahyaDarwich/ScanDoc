package com.document.scanner.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import com.document.scanner.R

sealed class DocumentScannerDestination(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unSelectedIconId: Int
) {
    object Documents :
        DocumentScannerDestination(
            "documents",
            R.string.documents,
            R.drawable.filled_picture_as_pdf,
            R.drawable.outline_picture_as_pdf
        )

    object Images : DocumentScannerDestination(
        "images",
        R.string.images,
        R.drawable.filled_image,
        R.drawable.outline_image
    )
}
