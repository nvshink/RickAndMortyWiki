package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterModel

interface CharacterSmallListUiState {
    val isLocal: Boolean

    data class LoadingState(
        override val isLocal: Boolean = false,
    ) : CharacterSmallListUiState

    data class SuccessState(
        val characterList: List<CharacterModel>,
        override val isLocal: Boolean
    ) : CharacterSmallListUiState

    data class ErrorState(
        val error: Exception?,
        override val isLocal: Boolean
    ) : CharacterSmallListUiState
}