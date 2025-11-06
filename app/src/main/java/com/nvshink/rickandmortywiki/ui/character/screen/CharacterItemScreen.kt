package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
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
    onBackPressed: () -> Unit
) {
    Box(modifier = modifier){
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            ItemScreenTopBar(
                contentType = contentType,
                onBackButtonClicked = onBackPressed
            )
            when (detailUiState) {
                is CharacterDetailUiState.LoadingState -> {
                    ItemLoadingScreen()
                }

                is CharacterDetailUiState.ViewState -> {
                    CharacterItemViewScreen(
                        character = detailUiState.character,
                        onNavigation = { destination: Any ->
                            navController.navigate(destination)
                        }
                    )
                }

                is CharacterDetailUiState.ErrorState -> {
                    ItemErrorScreen(errorMessage = detailUiState.errorMessage)
                }
            }
        }
    }
}