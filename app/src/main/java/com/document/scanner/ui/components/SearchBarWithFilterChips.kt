package com.document.scanner.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.document.scanner.R
import com.document.scanner.data.SearchFilter

@Composable
fun SearchBarWithFilterChips(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    filters: List<SearchFilter>,
    currentSelectedFilter: SearchFilter,
    onSelectFilter: (SearchFilter) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var isFiltersVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(10.dp, 10.dp, 10.dp, if (!isFiltersVisible) 0.dp else 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                value = it
                onSearch(value)
            },
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_field_placeHolder),
                    fontWeight = FontWeight.Normal
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFiltersVisible = !isFiltersVisible },
            singleLine = true
        )

        AnimatedVisibility(visible = !isFiltersVisible) {
            FilterChipsGroup(
                filters = filters,
                currentSelectedFilter = currentSelectedFilter,
                scrollState = scrollState,
                onSelectFilter = onSelectFilter
            )
        }
    }
}