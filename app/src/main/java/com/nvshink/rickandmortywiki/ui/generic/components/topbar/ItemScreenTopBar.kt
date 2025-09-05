package com.nvshink.rickandmortywiki.ui.generic.components.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@Composable
fun ItemScreenTopBar(
    modifier: Modifier = Modifier,
    contentType: ContentType,
    onBackButtonClicked: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackButtonClicked != null) {
            IconButton(onClick = { onBackButtonClicked() }) {
                Icon(
                    imageVector = when (contentType) {
                        ContentType.LIST_ONLY -> Icons.Filled.ArrowBack
                        ContentType.LIST_AND_DETAIL -> Icons.Filled.Close
                    },
                    contentDescription = null
                )
            }
        }
        Row {
            if (actions != null) actions()
        }
    }
}