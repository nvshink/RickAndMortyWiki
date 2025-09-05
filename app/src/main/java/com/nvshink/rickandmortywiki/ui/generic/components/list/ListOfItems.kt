package com.nvshink.rickandmortywiki.ui.generic.components.list

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreenColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ListOfItems(
    modifier: Modifier = Modifier,
    listOfItems: List<T>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    errorMessage: String?,
    onLoadMore: () -> Unit,
    hideToTopButton: () -> Unit,
    showToTopButton: () -> Unit,
    listItem: @Composable (T) -> Unit,
    emptyListIcon: ImageVector? = null,
    emptyListIconDescription: String = "",
    emptyListTitle: String? = "",
    listArrangement: Dp = 0.dp,
    colors: EmptyItemScreenColors,
    listTopContent: (@Composable () -> Unit),
    fab: (@Composable (Modifier) -> Unit)?
) {
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }
    if (isAtTop) {
        hideToTopButton()
    } else {
        showToTopButton()
    }
    Box(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            listTopContent()
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            ) {
                if (!isLoading && listOfItems.isEmpty()) {
                    EmptyItemScreen(
                        title = emptyListTitle,
                        icon = emptyListIcon,
                        iconDescription = emptyListIconDescription,
                        colors = colors
                    )
                } else {
                    InfinityLazyGrid(
                        items = listOfItems,
                        cellsArrangement = listArrangement,
                        listItem = listItem,
                        lazyGridState = lazyGridState,
                        onLoadMore = onLoadMore
                    )
                }
            }
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(20.dp).weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        }
        if (errorMessage != null) Text(
            errorMessage
        )
        if (fab != null) {
            fab(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
        AnimatedVisibility(
            visible = !isAtTop,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
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
}

@Composable
fun <T> InfinityLazyGrid(
    items: List<T>,
    lazyGridState: LazyGridState,
    cellsArrangement: Dp,
    listItem: @Composable (T) -> Unit,
    onLoadMore: () -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
        horizontalArrangement = Arrangement.spacedBy(cellsArrangement),
        verticalArrangement = Arrangement.spacedBy(cellsArrangement)
    ) {
        itemsIndexed(items) { index, item ->
            listItem(item)
        }
    }
    InfiniteListHandler(gridState = lazyGridState, onLoadMore = onLoadMore)
}

@Composable
fun InfiniteListHandler(
    gridState: LazyGridState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
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