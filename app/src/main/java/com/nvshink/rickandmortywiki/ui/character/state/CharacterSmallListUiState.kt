package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.SortTypes

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