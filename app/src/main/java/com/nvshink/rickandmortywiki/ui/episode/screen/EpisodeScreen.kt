package com.nvshink.rickandmortywiki.ui.episode.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListCardContent
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.episode.components.EpisodePageListCardContent
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListView
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.PageListTopBar
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.EpisodeItemScreenRoute

@Composable
fun EpisodeScreen(
    modifier: Modifier = Modifier,
    pageListUiState: EpisodePageListUiState,
    onPageListEvent: (EpisodePageListEvent) -> Unit,
    detailModifier: Modifier,
    contentType: ContentType
) {
    ListOfItems(
        modifier = modifier,
        detailModifier = detailModifier,
        listView = ListView.Column,
        listOfItems = pageListUiState.episodeList,
        isLoading = pageListUiState::class == EpisodePageListUiState.LoadingState::class,
        isRefreshing = pageListUiState.isRefreshing,
        onRefresh = { onPageListEvent(EpisodePageListEvent.RefreshList) },
        onLoadMore = { onPageListEvent(EpisodePageListEvent.LoadMore) },
        onOffline = { isLocal ->
            onPageListEvent(EpisodePageListEvent.SetIsLocal(isLocal = isLocal))
            onPageListEvent(EpisodePageListEvent.RefreshList)
        },
        errorMessage = if (pageListUiState is EpisodePageListUiState.ErrorState) pageListUiState.error?.message else null,
        emptyListTitle = stringResource(R.string.empty_list_title_characters),
        emptyListIcon = Icons.Filled.SearchOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_characters),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_location),
        emptyDetailIcon = Icons.Filled.Person,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_location),
        listArrangement = 10.dp,
        fab = null,
        listItem = { episode ->
            EpisodePageListCardContent(episode)
        },
        listItemRoute = { characterId ->
            EpisodeItemScreenRoute(characterId)
        },
        listId = { character ->
            character.id
        },
        listTopContent = {
            PageListTopBar(
                query = pageListUiState.searchBarText,
                placeholder = stringResource(R.string.searchbar_placeholder_episode),
                onQueryChange = {
                    onPageListEvent(
                        EpisodePageListEvent.SetSearchBarText(text = it)
                    )
                    onPageListEvent(
                        EpisodePageListEvent.SetFilter(
                            filter = pageListUiState.filter.copy(
                                name = it
                            )
                        )
                    )
                },
                onSearch = {
                    onPageListEvent(
                        EpisodePageListEvent.SetFilter(
                            filter = pageListUiState.filter.copy(
                                name = it
                            )
                        )
                    )
                },
                onOnlineButton = {
                    onPageListEvent(EpisodePageListEvent.SetIsLocal(false))
                    onPageListEvent(EpisodePageListEvent.RefreshList)
                },
                isLocal = pageListUiState.isLocal
            )
        }
    )
}