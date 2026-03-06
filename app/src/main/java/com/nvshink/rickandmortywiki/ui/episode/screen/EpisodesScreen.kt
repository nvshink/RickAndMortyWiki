package com.nvshink.rickandmortywiki.ui.episode.screen

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
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.episode.components.EpisodePageListCardContent
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListView
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.PageListTopBar
import com.nvshink.rickandmortywiki.ui.episode.components.EpisodeFilterDialog
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.EpisodeItemScreenRoute

@Composable
fun EpisodesScreen(
    modifier: Modifier = Modifier,
    pageListUiState: EpisodePageListUiState,
    episodes: LazyPagingItems<EpisodeModel>,
    onPageListEvent: (EpisodePageListEvent) -> Unit,
    detailModifier: Modifier = Modifier,
    listModifier: Modifier = Modifier,
    contentType: ContentType,
) {
    ListOfItems(
        modifier = modifier,
        listModifier = listModifier,
        detailModifier = detailModifier,
        listView = ListView.Column,
        listOfItems = episodes,
        itemIndex = { episode ->
            episode?.id
        },
        isRefreshing = episodes.loadState.refresh is LoadState.Loading,
        onRefresh = { onPageListEvent(EpisodePageListEvent.RefreshList(episodes = episodes)) },
        onRetry = { onPageListEvent(EpisodePageListEvent.RetryPageLoad(episodes = episodes)) },
        emptyListTitle = stringResource(R.string.empty_list_title_episodes),
        emptyListIcon = Icons.Filled.SearchOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_episodes),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_episode),
        emptyDetailIcon = Icons.Filled.Person,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_episode),
        listArrangement = 10.dp,
        fab = null,
        listItem = { episode ->
            EpisodePageListCardContent(episode)
        },
        listItemRoute = { episodeId ->
            EpisodeItemScreenRoute(episodeId)
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
                }
            )
        }
    )
    if (pageListUiState.isShowingFilter) Box(modifier = modifier) {
        EpisodeFilterDialog(
            episodePageListUiState = pageListUiState,
            onEpisodeListEvent = onPageListEvent,
            contentType = contentType
        )
    }
}