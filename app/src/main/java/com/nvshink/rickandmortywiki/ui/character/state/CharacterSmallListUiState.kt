package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface CharacterSmallListUiState {

    data object LoadingState : CharacterSmallListUiState

    data class SuccessState(
        val characterList: List<CharacterModel>,
    ) : CharacterSmallListUiState

    data class ErrorState(
        val message: String? = null,
        val exception: Exception? = null,
    ) : CharacterSmallListUiState
}