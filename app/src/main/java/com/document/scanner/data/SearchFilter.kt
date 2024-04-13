package com.document.scanner.data

import com.document.scanner.R

enum class SearchFilter(val resId: Int) {
    ALL(R.string.all_filter_chip),
    PDF(R.string.pdf_filter_chip),
    IMAGES(R.string.images_filter_chip)
}