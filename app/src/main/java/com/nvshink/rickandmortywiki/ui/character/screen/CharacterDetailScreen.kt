package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeSmallListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.DetailScreenTopBar
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemErrorScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemLoadingScreen
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    modifier: Modifier = Modifier,
    contentType: ContentType,
    navController: NavHostController,
    detailUiState: CharacterDetailUiState,
    episodeSmallListUiState: EpisodeSmallListUiState,
    onSmallListEvent: (EpisodeSmallListEvent) -> Unit,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        DetailScreenTopBar(
            contentType = contentType,
            onBackButtonClicked = onBackClick
        )
        when (detailUiState) {
            is CharacterDetailUiState.LoadingState -> {
                ItemLoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }

            is CharacterDetailUiState.ViewState -> {
                onSmallListEvent(EpisodeSmallListEvent.SetUrls(detailUiState.character.episode))
                CharacterDetailViewScreen(
                    character = detailUiState.character,
                    episodesUiState = episodeSmallListUiState,
                    onSmallListRefresh = { onSmallListEvent(EpisodeSmallListEvent.Refresh) },
                    onNavigation = { destination: Any ->
                        navController.navigate(destination)
                    }
                )
            }

            is CharacterDetailUiState.ErrorState -> {
                ItemErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    errorMessage = detailUiState.message ?: detailUiState.exception?.message ?: "",
                    onClick = onRefreshClick
                )
            }
        }
    }
}