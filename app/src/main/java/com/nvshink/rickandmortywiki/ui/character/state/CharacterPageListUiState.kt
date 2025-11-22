package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface CharacterPageListUiState {
    val characterList: List<CharacterModel>
    val currentCharacter: CharacterModel?
    val filter: CharacterFilterModel
    val isShowingFilter: Boolean
    val isAtTop: Boolean
    val isRefreshing: Boolean
    val isLocal: Boolean
    val searchBarText: String
    val searchBarFiltersText: String
    val contentType: ContentType

    data class LoadingState(
        override val characterList: List<CharacterModel> = emptyList(),
        override val currentCharacter: CharacterModel? = null,
        override val filter: CharacterFilterModel = CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        ),
        override val isShowingFilter: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val isLocal: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterPageListUiState

    data class SuccessState(
        override val characterList: List<CharacterModel> = emptyList(),
        override val currentCharacter: CharacterModel? = null,
        override val filter: CharacterFilterModel = CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        ),
        override val isShowingFilter: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val isLocal: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterPageListUiState

    data class ErrorState(
        val error: Exception? = null,
        override val characterList: List<CharacterModel> = emptyList(),
        override val currentCharacter: CharacterModel? = null,
        override val filter: CharacterFilterModel = CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        ),
        override val isShowingFilter: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val isLocal: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterPageListUiState
}