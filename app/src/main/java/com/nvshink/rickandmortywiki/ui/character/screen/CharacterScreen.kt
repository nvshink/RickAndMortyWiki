package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.components.CharacterFilterDialog
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListCardContent
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListTopBar
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreenColors
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CharacterScreen(
    modifier: Modifier = Modifier,
    characterPageListUiState: CharacterPageListUiState,
    onCharacterListEvent: (CharacterPageListEvent) -> Unit,
    detailModifier: Modifier,
    contentType: ContentType,
    innerPadding: PaddingValues
) {

    ListOfItems(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 5.dp),
        detailModifier = detailModifier,
        listOfItems = characterPageListUiState.characterList,
        isLoading = characterPageListUiState::class == CharacterPageListUiState.LoadingState::class,
        isRefreshing = characterPageListUiState.isRefreshing,
        onRefresh = { onCharacterListEvent(CharacterPageListEvent.RefreshList) },
        onLoadMore = { onCharacterListEvent(CharacterPageListEvent.LoadMore) },
        errorMessage = if (characterPageListUiState is CharacterPageListUiState.ErrorState) characterPageListUiState.error?.message else null,
        emptyListTitle = stringResource(R.string.empty_list_title_characters),
        emptyListIcon = Icons.Filled.SearchOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_characters),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_character),
        emptyDetailIcon = Icons.Filled.Person,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_character),
        listArrangement = 10.dp,
        colors = EmptyItemScreenColors(
            iconTintColor = MaterialTheme.colorScheme.onSurface,
            textColor = MaterialTheme.colorScheme.outline
        ),
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
            CharacterPageListTopBar(
                uiState = characterPageListUiState,
                onEvent = onCharacterListEvent
            )
        }
    )
    if (characterPageListUiState.isShowingFilter) Box(modifier = modifier) {
        CharacterFilterDialog(
            characterPageListUiState = characterPageListUiState,
            onCharacterListEvent = onCharacterListEvent,
            contentType = contentType
        )
    }

}

