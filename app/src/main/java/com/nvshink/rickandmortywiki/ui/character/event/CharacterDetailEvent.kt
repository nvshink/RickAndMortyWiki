package com.nvshink.rickandmortywiki.ui.character.event

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface CharacterDetailEvent {
    data class SetCharacter(val id: Int) : CharacterDetailEvent
    data class SetContentType(val contentType: ContentType) : CharacterDetailEvent
    data class SetIsLocal(val isLocal: Boolean) : CharacterDetailEvent
    data object Refresh: CharacterDetailEvent
}