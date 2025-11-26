package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nvshink.rickandmortywiki.ui.generic.components.box.ErrorBox
import com.nvshink.rickandmortywiki.ui.generic.components.box.LoadingBox

@Composable
fun <T> SmallListOfItems(
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
                listOfItems.forEach{ item ->
                    listItem(item)
                }
            }
        }
    }
}