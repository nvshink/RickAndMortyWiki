package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterModel

interface CharacterDetailUiState {

    data class LoadingState(
        val character: CharacterModel? = null,
    ) : CharacterDetailUiState

    data class ViewState(
        val character: CharacterModel,
    ) : CharacterDetailUiState

    data class ErrorState(
        val message: String? = null,
        val exception: Exception? = null,
    ) : CharacterDetailUiState
}