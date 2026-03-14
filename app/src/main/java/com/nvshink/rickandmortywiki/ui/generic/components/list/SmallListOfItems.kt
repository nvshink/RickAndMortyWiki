package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nvshink.rickandmortywiki.ui.generic.components.box.ErrorBox
import com.nvshink.rickandmortywiki.ui.generic.components.box.LoadingBox

@Composable
fun <T> SmallListOfItems(
    title: String? = null,
    isLoading: Boolean,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    listOfItems: List<T>,
    listItem: @Composable (T) -> Unit
) {
    when {
        isLoading -> {
            LoadingBox()
        }

        errorMessage != null -> {
            ErrorBox(errorMessage = errorMessage, onRetryClick = onRetryClick)
        }

        else -> {
            Column {
                if (title != null) Text(text = title, style = MaterialTheme.typography.titleMedium)
                listOfItems.forEach{ item ->
                    listItem(item)
                }
            }
        }
    }
}