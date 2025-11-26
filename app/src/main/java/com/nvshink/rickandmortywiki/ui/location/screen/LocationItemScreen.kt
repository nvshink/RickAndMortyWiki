package com.nvshink.rickandmortywiki.ui.location.screen

import androidx.compose.foundation.layout.Box
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
import com.nvshink.rickandmortywiki.ui.character.screen.CharacterItemViewScreen
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterDetailViewModel
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterSmallListViewModel
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.ItemScreenTopBar
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemErrorScreen
import com.nvshink.rickandmortywiki.ui.generic.screens.ItemLoadingScreen
import com.nvshink.rickandmortywiki.ui.location.state.LocationDetailUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationItemScreen(
    modifier: Modifier = Modifier,
    contentType: ContentType,
    detailUiState: LocationDetailUiState,
    navController: NavHostController,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ItemScreenTopBar(
            contentType = contentType,
            onBackButtonClicked = onBackClick
        )
        when (detailUiState) {
            is LocationDetailUiState.LoadingState -> {
                ItemLoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }

            is LocationDetailUiState.ViewState -> {
                val characterSmallListViewModel: CharacterSmallListViewModel = hiltViewModel()
                val characterSmallListUiState =
                    characterSmallListViewModel.uiState.collectAsState().value
                val onCharacterSmallListEvent = characterSmallListViewModel::onEvent
                onCharacterSmallListEvent(CharacterSmallListEvent.SetUrls(detailUiState.location.residents))
                LocationItemViewScreen(
                    location = detailUiState.location,
                    residentsUiState = characterSmallListUiState,
                    onSmallListRefresh = {onCharacterSmallListEvent(CharacterSmallListEvent.Refresh)},
                    onNavigation = { destination: Any ->
                        navController.navigate(destination)
                    }
                )
            }

            is LocationDetailUiState.ErrorState -> {
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