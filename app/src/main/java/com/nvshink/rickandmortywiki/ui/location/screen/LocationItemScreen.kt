package com.nvshink.rickandmortywiki.ui.location.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nvshink.rickandmortywiki.ui.character.screen.CharacterItemViewScreen
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
    onBackPressed: () -> Unit
) {

    Box(modifier = modifier){
        Column {
            ItemScreenTopBar(
                contentType = contentType,
                onBackButtonClicked = onBackPressed
            )
            when (detailUiState) {
                is LocationDetailUiState.LoadingState -> {
                    ItemLoadingScreen()
                }

                is LocationDetailUiState.ViewState -> {
                    LocationItemViewScreen(location = detailUiState.location)
                }

                is LocationDetailUiState.ErrorState -> {
                    ItemErrorScreen(errorMessage = detailUiState.errorMessage)
                }
            }
        }
    }
}