package com.document.scanner

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.document.scanner.data.ScannedFile
import com.document.scanner.router.ContentRouter
import com.document.scanner.ui.screens.DocumentScannerApp
import com.document.scanner.ui.theme.DocumentScannerTheme
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File

class MainActivity : ComponentActivity() {
    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
//            setOnExitAnimationListener { screen ->
//                val scaleX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 1f, 0f)
//                scaleX.duration = 300L
//                scaleX.doOnEnd { screen.remove() }
//
//                val scaleY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 1f, 0f)
//                scaleY.duration = 300L
//                scaleY.doOnEnd { screen.remove() }
//
//                scaleX.start()
//                scaleY.start()
//            }
        }

        enableEdgeToEdge()

        setContent {
            val scannedFiles = remember { mutableStateListOf<ScannedFile>() }
            scannedFiles.addAll(
                ContentRouter.getInstance(this).getScannedFiles()
            )

            val options = GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(true)
                .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
                .setScannerMode(SCANNER_MODE_FULL)
                .build()

            val scanner = GmsDocumentScanning.getClient(options)

            val scannerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = {
                    if (it.resultCode == RESULT_OK) {
                        val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)

                        result?.pages?.let { pages ->
                            for (page in pages) {
                                page.imageUri.path?.let { path ->
                                    val file = File(path)

                                    scannedFiles.add(
                                        0,
                                        ScannedFile(
                                            Uri.parse(path),
                                            getContentUri(file),
                                            file.length(),
                                            file.lastModified()
                                        )
                                    )
                                }
                            }
                        }

                        result?.pdf?.let { pdf ->
                            pdf.uri.path?.let { path ->
                                val file = File(path)

                                scannedFiles.add(
                                    0,
                                    ScannedFile(
                                        Uri.parse(path),
                                        getContentUri(file),
                                        file.length(),
                                        file.lastModified()
                                    )
                                )
                            }
                        }
                    }
                }
            )

            DocumentScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DocumentScannerApp(onClickScanButton = {
                        scanner.getStartScanIntent(this@MainActivity).addOnSuccessListener {
                            scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
                        }.addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, scannedFiles)
                }
            }
        }
    }

    private fun getContentUri(file: File): Uri {
        return ContentRouter.getInstance(this).getContentUri(file)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addWebsiteShortcut(
        websiteUrl: String,
        shortcutId: String,
        shortcutLabelName: String,
        shortcutResId: Int
    ) {
        val shortcutIntent = Intent(Intent.ACTION_VIEW)
        shortcutIntent.data = websiteUrl.toUri()

        val shortcutInfo = ShortcutInfo.Builder(this@MainActivity, shortcutId)
            .setShortLabel(shortcutLabelName)
            .setIcon(
                Icon.createWithResource(
                    this@MainActivity,
                    shortcutResId
                )
            ).setIntent(shortcutIntent).build()

        val shortcutManger = getSystemService(ShortcutManager::class.java) as ShortcutManager
        shortcutManger.requestPinShortcut(shortcutInfo, null)
    }
}