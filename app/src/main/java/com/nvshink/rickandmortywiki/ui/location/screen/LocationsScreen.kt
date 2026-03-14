package com.nvshink.rickandmortywiki.ui.location.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListView
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.PageListTopBar
import com.nvshink.rickandmortywiki.ui.location.component.LocationFilterDialog
import com.nvshink.rickandmortywiki.ui.location.component.LocationPageListCardContent
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.LocationItemScreenRoute

@Composable
fun LocationsScreen(
    modifier: Modifier = Modifier,
    pageListUiState: LocationPageListUiState,
    locations: LazyPagingItems<LocationModel>,
    onPageListEvent: (LocationPageListEvent) -> Unit,
    detailModifier: Modifier = Modifier,
    listModifier: Modifier = Modifier,
    contentType: ContentType
) {
    ListOfItems(
        modifier = modifier,
        listModifier = listModifier,
        detailModifier = detailModifier,
        contentType = contentType,
        listView = ListView.Column,
        listOfItems = locations,
        itemIndex = { location ->
            location?.id
        },
        isRefreshing = locations.loadState.refresh is LoadState.Loading,
        onRefresh = { onPageListEvent(LocationPageListEvent.RefreshList(locations = locations)) },
        onRetry = { onPageListEvent(LocationPageListEvent.RetryPageLoad(locations = locations)) },
        emptyListTitle = stringResource(R.string.empty_list_title_locations),
        emptyListIcon = Icons.Filled.SearchOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_locations),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_location),
        emptyDetailIcon = Icons.Filled.Person,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_location),
        listArrangement = 10.dp,
        listItem = {location ->
            LocationPageListCardContent(location = location)
        },
        listItemRoute = { locationId ->
            LocationItemScreenRoute(locationId)
        },
        listTopContent = {
            PageListTopBar(
                query = pageListUiState.searchBarText,
                placeholder = stringResource(R.string.searchbar_placeholder_location),
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
            )
        }
    )
    if (pageListUiState.isShowingFilter) Box(modifier = modifier) {
        LocationFilterDialog(
            locationPageListUiState = pageListUiState,
            onLocationListEvent = onPageListEvent,
            contentType = contentType
        )
    }
}