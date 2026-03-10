package com.nvshink.rickandmortywiki.ui.generic.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageListTopBar(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFilterButton: (() -> Unit)? = null
) {
    //Clear the autofocus set by AnimatedPane from ListOfItems
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        focusManager.clearFocus()
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SearchBar(
                modifier = Modifier.weight(1f),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.Companion,
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = onSearch,
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text(text = placeholder) })
                },
                expanded = false,
                onExpandedChange = {},
                windowInsets = WindowInsets(0, 0, 0, 0),
            ) { }
            if (onFilterButton != null) {
                Button(
                    onClick = onFilterButton,
                    modifier = Modifier.Companion.size(48.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null
                    )
                }
            }
        }
    }
}