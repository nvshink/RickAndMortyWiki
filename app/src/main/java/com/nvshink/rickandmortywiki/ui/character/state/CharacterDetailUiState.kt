package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterLocationModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import java.time.ZonedDateTime

interface CharacterDetailUiState {
    val isLocal: Boolean
    val contentType: ContentType

    data class LoadingState(
        override val isLocal: Boolean = false,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterDetailUiState

    data class ViewState(
        val character: CharacterModel,
        override val isLocal: Boolean,
        override val contentType: ContentType
    ) : CharacterDetailUiState

    data class ErrorState(
        val error: Exception,
        override val isLocal: Boolean,
        override val contentType: ContentType
    ) : CharacterDetailUiState
}