package com.document.scanner.ui.Dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.document.scanner.R
import kotlinx.coroutines.delay

@Composable
fun CustomDialog(
    showDialog: Boolean = false,
    defaultValue: String = "",
    dialogTitle: String,
    textFieldLabel: String,
    confirmBtnText: String = stringResource(id = R.string.edit_dialog_save_btn),
    cancelBtnText: String = stringResource(id = R.string.edit_dialog_cancel_btn),
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var value by remember { mutableStateOf(defaultValue) }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(50)
            focusRequester.requestFocus()
        }

        AlertDialog(
            title = { Text(text = dialogTitle) },
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = {
                        Text(text = textFieldLabel)
                    },
                    value = value,
                    onValueChange = { value = it }
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = { onConfirm(value) }) {
                    Text(text = confirmBtnText)
                }
            },
            dismissButton = {
                Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = onDismiss) {
                    Text(text = cancelBtnText)
                }
            })
    }
}