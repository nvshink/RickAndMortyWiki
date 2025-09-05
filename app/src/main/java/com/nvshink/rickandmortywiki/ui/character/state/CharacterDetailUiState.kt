package com.nvshink.rickandmortywiki.ui.character.state

import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterLocationModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import java.time.ZonedDateTime

interface CharacterDetailUiState {
    val character: CharacterModel?
    val contentType: ContentType

    data class LoadingState(
        override val character: CharacterModel? = null,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterDetailUiState

    data class ViewState(
        override val character: CharacterModel = CharacterModel(
            id = 0,
            name = "",
            status = CharacterStatus.UNKNOWN,
            species = "",
            type = "",
            gender = CharacterGender.UNKNOWN,
            origin = CharacterLocationModel(
                id = 0,
                name = "",
                url = ""
            ),
            location = CharacterLocationModel(
                id = 0,
                name = "",
                url = ""
            ),
            image = "",
            episode = emptyList(),
            url = "",
            created = ZonedDateTime.now()
        ),
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterDetailUiState

    data class ErrorState(
        val errorMessage: String = "",
        override val character: CharacterModel? = null,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : CharacterDetailUiState
}