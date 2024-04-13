package com.document.scanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.document.scanner.data.SearchFilter

@Composable
fun FilterChipsGroup(
    scrollState: ScrollState,
    filters: List<SearchFilter>,
    currentSelectedFilter: SearchFilter,
    onSelectFilter: (SearchFilter) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(0.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        filters.forEach { searchFilter ->
            SearchFilterChip(
                label = stringResource(id = searchFilter.resId),
                onSelectFilter = { onSelectFilter(searchFilter) },
                isSelected = searchFilter == currentSelectedFilter
            )
        }
    }
}

@Preview
@Composable
fun SearchFilterChip(
    label: String = "test",
    onSelectFilter: () -> Unit = {},
    isSelected: Boolean = false
) {
    FilterChip(
        selected = isSelected,
        onClick = onSelectFilter,
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer)
        else ButtonDefaults.outlinedButtonBorder,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.background
        ),
        label = {
            Text(
                text = label, color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        })
}