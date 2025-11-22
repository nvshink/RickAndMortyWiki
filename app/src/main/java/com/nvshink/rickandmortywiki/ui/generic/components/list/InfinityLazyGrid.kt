package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.generic.components.box.ErrorBox
import com.nvshink.rickandmortywiki.ui.generic.components.box.LoadingBox
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun <T> InfinityLazyGrid(
    modifier: Modifier = Modifier,
    items: List<T>,
    cellsArrangement: Dp,
    listItem: @Composable (T) -> Unit,
    listTopContent: (@Composable () -> Unit)? = null,
    isLoading: Boolean,
    emptyListIcon: ImageVector? = null,
    emptyListIconDescription: String = "",
    emptyListTitle: String? = "",
    errorMessage: String?,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onOffline: (Boolean) -> Unit
) {
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = modifier) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2),
                state = lazyGridState,
                horizontalArrangement = Arrangement.spacedBy(cellsArrangement),
                verticalArrangement = Arrangement.spacedBy(cellsArrangement)
            ) {
                if (listTopContent != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        listTopContent()
                    }
                }
                if (!isLoading && errorMessage.isNullOrBlank() && items.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyItemScreen(
                            modifier = Modifier.fillMaxSize(),
                            title = emptyListTitle,
                            icon = emptyListIcon,
                            iconDescription = emptyListIconDescription,
                            onRefresh = onRefresh
                        )
                    }
                } else {
                    itemsIndexed(items = items, contentType = { index, item -> item }) { index, item ->
                        listItem(item)
                    }
                }
                if (errorMessage != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorBox(
                            errorMessage = errorMessage,
                            onRetryClick = onLoadMore,
                            onOfflineClick = onOffline
                        )
                    }
                }
                if (isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LoadingBox()
                    }
                }
        }
        InfiniteListHandler(lazyGridState = lazyGridState, onLoadMore = onLoadMore)

        ToTopBottomGrid(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            lazyGridState = lazyGridState,
            onClick = {
                coroutineScope.launch {
                    lazyGridState.scrollToItem(0)
                }
            }
        )
    }
}

@Composable
private fun InfiniteListHandler(
    lazyGridState: LazyGridState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyGridState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                onLoadMore()
            }
    }
}


@Composable
private fun ToTopBottomGrid(
    modifier: Modifier,
    lazyGridState: LazyGridState,
    onClick: () -> Unit
) {
    val isScrollToTop by remember {
        derivedStateOf {
            lazyGridState.lastScrolledBackward && lazyGridState.firstVisibleItemScrollOffset != 0 && lazyGridState.firstVisibleItemIndex != 0
        }
    }
    AnimatedVisibility(
        visible = isScrollToTop,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = stringResource(R.string.icon_description_arrow_upward),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}