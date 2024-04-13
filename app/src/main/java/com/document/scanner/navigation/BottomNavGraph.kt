package com.document.scanner.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.document.scanner.data.ScannedFile
import com.document.scanner.ui.screens.ScannedFilesScreen
import com.document.scanner.ui.screens.ImagesScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    scannedDocumentsFiles: MutableList<ScannedFile>,
    scannedImagesFiles: MutableList<ScannedFile>,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = DocumentScannerDestination.Documents.route
    ) {
        composable(route = DocumentScannerDestination.Documents.route) {
            ScannedFilesScreen(scannedDocumentsFiles, contentPadding)
        }
        composable(route = DocumentScannerDestination.Images.route) {
            ImagesScreen(scannedImagesFiles, contentPadding)
        }
    }
}