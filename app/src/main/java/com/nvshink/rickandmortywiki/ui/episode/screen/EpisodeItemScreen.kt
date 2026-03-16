package com.nvshink.rickandmortywiki.ui.episode.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.DetailScreenTopBar
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemErrorScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemLoadingScreen
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeDetailUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeItemScreen(
    modifier: Modifier = Modifier,
    contentType: ContentType,
    detailUiState: EpisodeDetailUiState,
    characterSmallListUiState: CharacterSmallListUiState,
    onSmallListEvent: (CharacterSmallListEvent) -> Unit,
    navController: NavHostController,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        DetailScreenTopBar(
            contentType = contentType,
            onBackButtonClicked = onBackClick
        )
        when (detailUiState) {
            is EpisodeDetailUiState.LoadingState -> {
                ItemLoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }

            is EpisodeDetailUiState.ViewState -> {
                onSmallListEvent(CharacterSmallListEvent.SetUrls(detailUiState.episode.characters))
                EpisodeItemViewScreen(
                    episode = detailUiState.episode,
                    charactersUiState = characterSmallListUiState,
                    onSmallListRefresh = { onSmallListEvent(CharacterSmallListEvent.Refresh) },
                    onNavigation = { destination: Any ->
                        navController.navigate(destination)
                    }
                )
            }

            is EpisodeDetailUiState.ErrorState -> {
                ItemErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    errorMessage = detailUiState.exception?.message ?: "",
                    onClick = onRefreshClick
                )
            }
        }
    }
}