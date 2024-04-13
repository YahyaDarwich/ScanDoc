package com.document.scanner.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.document.scanner.R
import com.document.scanner.data.ScannedFile
import com.document.scanner.utils.Utils

//@Preview
@SuppressLint("QueryPermissionsNeeded")
@Composable
fun FileCell(
    data: ScannedFile = ScannedFile(),
    onItemClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onEdit: (Uri) -> Unit = {},
    onDelete: (Uri) -> Unit = {}
) {
    val isPdf = data.fileUri.lastPathSegment?.endsWith("pdf") == true
    var isMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onItemClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isPdf) {
                    Icon(
                        painterResource(id = R.drawable.filled_picture_as_pdf),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(15.dp),
                        tint = Color.Red
                    )
                } else
                    Image(
                        painter = rememberAsyncImagePainter(model = data.contentUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = data.fileUri.lastPathSegment.toString(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    fontStyle = FontStyle.Normal, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = Utils.longToDate(data.lastModifiedDate) + " • " + Utils.formatFileSize(
                        data.totalSize
                    ), fontWeight = FontWeight.Thin,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    fontStyle = FontStyle.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                IconButton(onClick = onShareClick) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = null)
                }

                IconButton(
                    onClick = { isMenuExpanded = !isMenuExpanded }
                ) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }) {

                    AddDropDownMenuItem(
                        text = stringResource(id = R.string.edit_file_dropdown),
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onclick = {
                            isMenuExpanded = false
                            onEdit(data.fileUri)
                        }
                    )

                    AddDropDownMenuItem(
                        text = stringResource(id = R.string.delete_file_dropdown),
                        textColor = Color.Red,
                        onclick = {
                            isMenuExpanded = false
                            onDelete(data.fileUri)
                        })
                }
            }
        }
    }
}

@Composable
fun AddDropDownMenuItem(
    text: String,
    textColor: Color,
    textSize: TextUnit = 16.sp,
    onclick: () -> Unit = {}
) {
    DropdownMenuItem(onClick = onclick) {
        Text(text = text, color = textColor, fontSize = textSize, textAlign = TextAlign.Center)
    }
}