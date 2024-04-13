package com.document.scanner.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.document.scanner.R
import com.document.scanner.data.ScannedFile

@Composable
fun ImagesScreen(
    scannedImagesFiles: MutableList<ScannedFile>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (scannedImagesFiles.isEmpty()) {
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.empty_list))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                isPlaying = true,
                iterations = LottieConstants.IterateForever
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                LottieAnimation(
                    progress = progress,
                    composition = composition,
                    modifier = Modifier
                        .requiredHeight(250.dp)
                        .requiredWidth(250.dp)
                )

                Text(
                    text = "No Images available scan to add more",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val context = LocalContext.current
            val state = remember { mutableStateOf(scannedImagesFiles.reversed()) }

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF1FAEE))
                    .padding(vertical = 16.dp, horizontal = 10.dp),
                columns = GridCells.FixedSize(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = contentPadding,
            ) {
                items(state.value) {
                    ImageCell(
                        data = it,
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(), onClickImage = {
                            val viewImage = Intent(Intent.ACTION_VIEW).apply {
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                setDataAndType(it.contentUri, "image/*")
                            }

                            if (viewImage.resolveActivity(context.packageManager) != null) {
                                context.startActivity(viewImage)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.no_gallery_app),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCell(modifier: Modifier = Modifier, data: ScannedFile, onClickImage: () -> Unit) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(model = data.contentUri)
    Card(modifier = modifier, shape = RoundedCornerShape(8.dp), onClick = onClickImage) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}