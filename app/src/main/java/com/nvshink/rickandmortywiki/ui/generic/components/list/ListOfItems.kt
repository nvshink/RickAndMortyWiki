package com.nvshink.rickandmortywiki.ui.generic.components.list

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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.screen.CharacterItemScreen
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterDetailViewModel
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreenColors
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.screen.LocationItemScreen
import com.nvshink.rickandmortywiki.ui.location.viewmodel.LocationDetailViewModel
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.LocationItemScreenRoute
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> ListOfItems(
    modifier: Modifier = Modifier,
    listOfItems: List<T>,
    listItem: @Composable (T) -> Unit,
    listTopContent: @Composable () -> Unit = @Composable {},
    detail: @Composable (Int, () -> Unit) -> Unit,
    scaffoldNavigator: ThreePaneScaffoldNavigator<Int>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    errorMessage: String?,
    emptyListIcon: ImageVector? = null,
    emptyListIconDescription: String = "",
    emptyListTitle: String? = "",
    emptyDetailIcon: ImageVector? = null,
    emptyDetailIconDescription: String = "",
    emptyDetailTitle: String? = "",
    listArrangement: Dp = 0.dp,
    colors: EmptyItemScreenColors,
    fab: (@Composable (Modifier) -> Unit)?
) {
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }
    NavigableListDetailPaneScaffold(
        modifier = modifier,
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                Box {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
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
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                                    .weight(1f), contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
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
        },
        detailPane = {
            AnimatedPane {
                scaffoldNavigator.currentDestination?.contentKey?.let {
                    detail(it) { coroutineScope.launch { scaffoldNavigator.navigateBack() } }
                } ?: EmptyItemScreen(
                    title = emptyDetailTitle,
                    icon = emptyDetailIcon,
                    iconDescription = emptyDetailIconDescription
                )
            }

        }
    )
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