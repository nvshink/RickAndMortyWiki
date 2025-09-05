package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.SortTypes

interface CharacterSmallListUiState {
    val sortType: SortTypes
    val sortFields: CharacterSortFields

    data class LoadingState(
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: CharacterSortFields = CharacterSortFields.NAME
    ) : CharacterSmallListUiState

    data class SuccessState(
        val characterList: List<CharacterModel> = emptyList(),
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: CharacterSortFields = CharacterSortFields.NAME
    ) : CharacterSmallListUiState

    data class ErrorState(
        val error: Throwable? = null,
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: CharacterSortFields = CharacterSortFields.NAME
    ) : CharacterSmallListUiState
}