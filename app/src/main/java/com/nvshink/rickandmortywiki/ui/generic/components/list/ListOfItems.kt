package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.ui.generic.components.navigation.DynamicNavigation
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreenColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> ListOfItems(
    modifier: Modifier = Modifier,
    detailModifier: Modifier = Modifier,
    listOfItems: List<T>,
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
    fab: (@Composable (Modifier) -> Unit)?,
    listItem: @Composable (T) -> Unit,
    listItemRoute: (Int) -> Any,
    listId: (T) -> Int,
    listTopContent: @Composable () -> Unit = @Composable {}
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Int>()

    NavigableListDetailPaneScaffold(
        modifier = modifier,
        navigator = scaffoldNavigator,
        listPane = {
            //TODO: the text field is auto focused when it is in AnimatedPane. It's problem
            AnimatedPane {
                Box {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = onRefresh
                        ) {
                            Column {
                                if (!isLoading && listOfItems.isEmpty()) {
                                    EmptyItemScreen(
                                        title = emptyListTitle,
                                        icon = emptyListIcon,
                                        iconDescription = emptyListIconDescription,
                                        colors = colors,
                                        onRefresh = onRefresh
                                    )
                                } else {
                                    InfinityLazyGrid(
                                        items = listOfItems,
                                        cellsArrangement = listArrangement,
                                        listItem = { item ->
                                            ListItem(
                                                onCardClick = {
                                                    coroutineScope.launch {
                                                        scaffoldNavigator.navigateTo(
                                                            pane = ListDetailPaneScaffoldRole.Detail,
                                                            contentKey = listId(item)
                                                        )
                                                    }
                                                }
                                            ) {
                                                listItem(item)
                                            }
                                        },
                                        listTopContent = listTopContent,
                                        isLoading = isLoading,
                                        errorMessage = errorMessage,
                                        onLoadMore = onLoadMore
                                    )
                                }
                            }
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

                }

            }


        },
        detailPane = {
            val id = scaffoldNavigator.currentDestination?.contentKey
            AnimatedPane {
                if (id != null) {
                    key(id) {
                        DynamicNavigation(
                            itemModifier = detailModifier,
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