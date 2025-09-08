package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SmallListOfItems(
    isLoading: Boolean,
    errorMessage: String?,
    listOfItems: List<Any>,
    listItem: @Composable (Any) -> Unit
) {
    when {
        isLoading -> {
            CircularProgressIndicator()
        }

        errorMessage != null -> {
            Text(errorMessage)
        }

        else -> {
            LazyColumn {
                itemsIndexed(listOfItems){ index, item ->
                    listItem(item)
                }
            }
        }
    }
}