package com.nvshink.rickandmortywiki.ui.location.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.PageListTopBar
import com.nvshink.rickandmortywiki.ui.location.component.LocationPageListCardContent
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.LocationItemScreenRoute

@Composable
fun LocationScreen(
    modifier: Modifier = Modifier,
    pageListUiState: LocationPageListUiState,
    onPageListEvent: (LocationPageListEvent) -> Unit,
    detailModifier: Modifier,
    contentType: ContentType,
) {
    ListOfItems(
        modifier = modifier,
        detailModifier = detailModifier,
        listOfItems = pageListUiState.locationList,
        isLoading = pageListUiState::class == LocationPageListUiState.LoadingState::class,
        isRefreshing = pageListUiState.isRefreshing,
        onRefresh = { onPageListEvent(LocationPageListEvent.RefreshList) },
        onLoadMore = { onPageListEvent(LocationPageListEvent.LoadMore) },
        onOffline = { isLocal ->
            onPageListEvent(LocationPageListEvent.SetIsLocal(isLocal))
            onPageListEvent(LocationPageListEvent.RefreshList)
        },
        errorMessage = if (pageListUiState is LocationPageListUiState.ErrorState) pageListUiState.error?.message else null,
        emptyListTitle = stringResource(R.string.empty_list_title_characters),
        emptyListIcon = Icons.Filled.LocationOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_characters),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_location),
        emptyDetailIcon = Icons.Filled.LocationOn,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_location),
        listArrangement = 10.dp,
        fab = null,
        listItem = {location ->
            LocationPageListCardContent(location = location)
        },
        listItemRoute = { locationId ->
            LocationItemScreenRoute(locationId)
        },
        listId = { location ->
            location.id
        },
        listTopContent = {
            PageListTopBar(
                query = pageListUiState.searchBarText,
                onQueryChange = {
                    onPageListEvent(
                        LocationPageListEvent.SetSearchBarText(text = it)
                    )
                    onPageListEvent(
                        LocationPageListEvent.SetFilter(
                            filter = pageListUiState.filter.copy(
                                name = it
                            )
                        )
                    )
                },
                onSearch = {
                    onPageListEvent(
                        LocationPageListEvent.SetFilter(
                            filter = pageListUiState.filter.copy(
                                name = it
                            )
                        )
                    )
                },
                onFilterButton = { onPageListEvent(LocationPageListEvent.ShowFilterDialog) },
                onOnlineButton = {
                    onPageListEvent(LocationPageListEvent.SetIsLocal(false))
                    onPageListEvent(LocationPageListEvent.RefreshList)
                },
                isLocal = pageListUiState.isLocal
            )
        },
    )
}