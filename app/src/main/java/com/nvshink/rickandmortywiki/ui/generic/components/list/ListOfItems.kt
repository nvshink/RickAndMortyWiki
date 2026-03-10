package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.nvshink.rickandmortywiki.ui.generic.components.navigation.DynamicNavigation
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun <T : Any> ListOfItems(
    modifier: Modifier = Modifier,
    listModifier: Modifier = Modifier,
    detailModifier: Modifier = Modifier,
    contentType: ContentType,
    listView: ListView = ListView.Column,
    listOfItems: LazyPagingItems<T>,
    itemIndex: (T?) -> Int?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    emptyListIcon: ImageVector? = null,
    emptyListIconDescription: String = "",
    emptyListTitle: String? = "",
    emptyDetailIcon: ImageVector? = null,
    emptyDetailIconDescription: String = "",
    emptyDetailTitle: String? = "",
    listArrangement: Dp = 0.dp,
    fab: (@Composable (Modifier) -> Unit)?,
    listItem: @Composable (T) -> Unit,
    listItemRoute: (Int) -> Any,
    listTopContent: @Composable () -> Unit = @Composable {}
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val pullToRefreshState = rememberPullToRefreshState()



    val willRefresh by remember {
        derivedStateOf {
            pullToRefreshState.distanceFraction > 1f
        }
    }

    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(willRefresh) {
        when {
            willRefresh -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(70)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(100)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            !isRefreshing && pullToRefreshState.distanceFraction > 0f -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }


    NavigableListDetailPaneScaffold(
        modifier = modifier,
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = onRefresh,
                            state = pullToRefreshState,
                            indicator = {
                                PullToRefreshDefaults.LoadingIndicator(
                                    state = pullToRefreshState,
                                    isRefreshing = isRefreshing,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                        )
                        {
                            when (listView) {
                                ListView.Column -> {
                                    val lazyListState = rememberLazyListState()
                                    InfinityLazyColumn(
                                        state = lazyListState,
                                        modifier = listModifier,
                                        items = listOfItems,
                                        itemIndex = itemIndex,
                                        contentArrangement = listArrangement,
                                        listItem = { item ->
                                            val index = itemIndex(item)
                                            ListItem(
                                                onCardClick = if (index != null) {
                                                    {
                                                        coroutineScope.launch {
                                                            scaffoldNavigator.navigateTo(
                                                                pane = ListDetailPaneScaffoldRole.Detail,
                                                                contentKey = index
                                                            )
                                                        }
                                                    }
                                                } else null
                                            ) {
                                                listItem(item)
                                            }
                                        },
                                        listTopContent = listTopContent,
                                        emptyListIcon = emptyListIcon,
                                        emptyListIconDescription = emptyListIconDescription,
                                        emptyListTitle = emptyListTitle,
                                        onLoadMore = onRetry,
                                        onRefresh = onRefresh,
                                        pullToRefreshState = pullToRefreshState
                                    )
                                }

                                ListView.Grid -> {
                                    val lazyGridState = rememberLazyGridState()
                                    InfinityLazyGrid(
                                        state = lazyGridState,
                                        modifier = listModifier,
                                        items = listOfItems,
                                        itemIndex = itemIndex,
                                        cellsArrangement = listArrangement,
                                        listItem = { item ->
                                            val index = itemIndex(item)
                                            ListItem(
                                                onCardClick = if (index != null) {
                                                    {
                                                        coroutineScope.launch {
                                                            scaffoldNavigator.navigateTo(
                                                                pane = ListDetailPaneScaffoldRole.Detail,
                                                                contentKey = index
                                                            )
                                                        }
                                                    }
                                                } else null
                                            ) {
                                                listItem(item)
                                            }
                                        },
                                        listTopContent = listTopContent,
                                        emptyListIcon = emptyListIcon,
                                        emptyListIconDescription = emptyListIconDescription,
                                        emptyListTitle = emptyListTitle,
                                        onLoadMore = onRetry,
                                        onRefresh = onRefresh,
                                        pullToRefreshState = pullToRefreshState
                                    )
                                }
                            }
                        }
                    }
                    if (fab != null) {
                        fab(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val id = scaffoldNavigator.currentDestination?.contentKey
                if (id != null) {
                    key(id) {
                        DynamicNavigation(
                            modifier = Modifier.fillMaxSize(),
                            itemModifier = detailModifier,
                            contentType = contentType,
                            startDestination = listItemRoute(id),
                            onBack = {
                                coroutineScope.launch {
                                    scaffoldNavigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.List,
                                    )
                                }
                            }
                        )
                    }
                } else EmptyItemScreen(
                    title = emptyDetailTitle,
                    icon = emptyDetailIcon,
                    iconDescription = emptyDetailIconDescription
                )
            }
        }
    )
}
