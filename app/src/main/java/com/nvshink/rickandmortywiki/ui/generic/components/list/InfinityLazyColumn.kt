package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.nvshink.data.generic.network.exception.ResourceNotFoundException
import com.nvshink.rickandmortywiki.ui.generic.components.box.ErrorBox
import com.nvshink.rickandmortywiki.ui.generic.components.box.LoadingBox
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemErrorScreen
import kotlin.math.roundToInt

@Composable
fun <T : Any> InfinityLazyColumn(
    state: LazyListState,
    modifier: Modifier = Modifier,
    items: LazyPagingItems<T>,
    itemIndex: (T?) -> Int?,
    contentArrangement: Dp = 5.dp,
    listItem: @Composable (T) -> Unit,
    listTopContent: (@Composable () -> Unit)? = null,
    emptyListIcon: ImageVector? = null,
    emptyListIconDescription: String = "",
    emptyListTitle: String? = "",
    onLoadMore: () -> Unit = {},
    onRefresh: () -> Unit = {},
    pullToRefreshState: PullToRefreshState
) {
    val density = LocalDensity.current

    val targetOffset by remember {
        derivedStateOf {
            val fraction = pullToRefreshState.distanceFraction
            when {
                fraction in 0f..1f -> (250 * fraction).roundToInt()
                fraction > 1f -> (250 + ((fraction - 1f) * .1f) * 100).roundToInt()
                else -> 0
            }
        }
    }

    val cardOffset by animateIntAsState(
        targetValue = targetOffset,
        label = "cardOffset"
    )
    LazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(contentArrangement)
    ) {
        item(key = "top_content") {
            listTopContent?.invoke()
        }
        items(
            count = items.itemCount,
            key = items.itemKey { item -> itemIndex(item) ?: item.hashCode() }
        ) { index ->
            val item = items[index]
            val yFactor1 = (index + 2) / 20f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY =
                            (cardOffset * yFactor1) * density.density
                    }
            ) { key(itemIndex(item)) { item?.let { listItem(item) } } }
        }

        when {
            items.loadState.refresh is LoadState.Loading -> {
                item(key = "loading_refresh") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingBox()
                    }
                }
            }

            items.loadState.refresh is LoadState.Error -> {
                item(key = "error_refresh") {
                    val e = items.loadState.refresh as LoadState.Error
                    if (e.error is ResourceNotFoundException) {
                        EmptyItemScreen(
                            title = emptyListTitle,
                            icon = emptyListIcon,
                            iconDescription = emptyListIconDescription,
                            onRefresh = onRefresh
                        )
                    } else {
                        ItemErrorScreen(
                            modifier = Modifier.fillMaxSize(),
                            errorMessage = e.error.localizedMessage ?: "",
                            onClick = onRefresh
                        )
                    }
                }
            }

            items.loadState.append is LoadState.Loading -> {
                item(key = "loading_append") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingBox()
                    }
                }
            }

            items.loadState.append is LoadState.Error -> {
                item(key = "error_append") {
                    val e = items.loadState.append as LoadState.Error
                    ErrorBox(
                        errorMessage = e.error.localizedMessage ?: "",
                        onRetryClick = onLoadMore
                    )
                }
            }
        }
    }
}