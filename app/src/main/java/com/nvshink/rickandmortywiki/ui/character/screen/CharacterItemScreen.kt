package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterSmallListViewModel
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeSmallListEvent
import com.nvshink.rickandmortywiki.ui.episode.viewmodel.EpisodeSmallListViewModel
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.ItemScreenTopBar
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemErrorScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemLoadingScreen
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterItemScreen(
    modifier: Modifier = Modifier,
    contentType: ContentType,
    navController: NavHostController,
    detailUiState: CharacterDetailUiState,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ItemScreenTopBar(
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
                val episodeSmallListViewModel: EpisodeSmallListViewModel = hiltViewModel()
                val episodeSmallListUiState =
                    episodeSmallListViewModel.uiState.collectAsState().value
                val onEpisodeSmallListEvent = episodeSmallListViewModel::onEvent
                onEpisodeSmallListEvent(EpisodeSmallListEvent.SetUrls(detailUiState.character.episode))
                CharacterItemViewScreen(
                    character = detailUiState.character,
                    episodesUiState = episodeSmallListUiState,
                    onSmallListRefresh = { onEpisodeSmallListEvent(EpisodeSmallListEvent.Refresh)},
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
                    errorMessage = detailUiState.error.message ?: "",
                    onClick = onRefreshClick
                )
            }
        }
    }
}