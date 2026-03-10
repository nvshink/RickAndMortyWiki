package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterLocationModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import java.time.ZonedDateTime

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