package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.components.CharacterFilterDialog
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListCardContent
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.PageListTopBar
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListView
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    pageListUiState: CharacterPageListUiState,
    onPageListEvent: (CharacterPageListEvent) -> Unit,
    detailModifier: Modifier = Modifier,
    listModifier: Modifier = Modifier,
    contentType: ContentType,
) {
    ListOfItems(
        modifier = modifier,
        listModifier = listModifier,
        detailModifier = detailModifier,
        listView = ListView.Grid,
        listOfItems = pageListUiState.characterList,
        isLoading = pageListUiState::class == CharacterPageListUiState.LoadingState::class,
        isRefreshing = pageListUiState.isRefreshing,
        onRefresh = { onPageListEvent(CharacterPageListEvent.RefreshList) },
        onLoadMore = { onPageListEvent(CharacterPageListEvent.LoadMore) },
        onOffline = { isLocal ->
            onPageListEvent(CharacterPageListEvent.SetIsLocal(isLocal = isLocal))
            onPageListEvent(CharacterPageListEvent.RefreshList)
        },
        errorMessage = if (pageListUiState is CharacterPageListUiState.ErrorState) pageListUiState.error?.message else null,
        emptyListTitle = stringResource(R.string.empty_list_title_characters),
        emptyListIcon = Icons.Filled.SearchOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_characters),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_location),
        emptyDetailIcon = Icons.Filled.Person,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_location),
        listArrangement = 10.dp,
        fab = null,
        listItem = { character ->
            CharacterPageListCardContent(character)
        },
        listItemRoute = { characterId ->
            CharacterItemScreenRoute(characterId)
        },
        listId = { character ->
            character.id
        },
        listTopContent = {
            PageListTopBar(
                query = pageListUiState.searchBarText,
                placeholder = stringResource(R.string.searchbar_placeholder_character),
                onQueryChange = {
                    onPageListEvent(
                        CharacterPageListEvent.SetSearchBarText(text = it)
                    )
                    onPageListEvent(
                        CharacterPageListEvent.SetFilter(
                            filter = pageListUiState.filter.copy(
                                name = it
                            )
                        )
                    )
                },
                onSearch = {
                    onPageListEvent(
                        CharacterPageListEvent.SetFilter(
                            filter = pageListUiState.filter.copy(
                                name = it
                            )
                        )
                    )
                },
                onFilterButton = { onPageListEvent(CharacterPageListEvent.ShowFilterDialog) },
                onOnlineButton = {
                    onPageListEvent(CharacterPageListEvent.SetIsLocal(false))
                    onPageListEvent(CharacterPageListEvent.RefreshList)
                },
                isLocal = pageListUiState.isLocal
            )
        }
    )
    if (pageListUiState.isShowingFilter) Box(modifier = modifier) {
        CharacterFilterDialog(
            characterPageListUiState = pageListUiState,
            onCharacterListEvent = onPageListEvent,
            contentType = contentType
        )
    }

}

