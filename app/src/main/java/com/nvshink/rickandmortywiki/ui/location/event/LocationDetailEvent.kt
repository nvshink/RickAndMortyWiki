package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface LocationDetailEvent {
    data class SetLocation(val id: Int) : LocationDetailEvent
    data class SetContentType(val contentType: ContentType) : LocationDetailEvent
    data class SetIsLocal(val isLocal: Boolean) : LocationDetailEvent
    data object Refresh: LocationDetailEvent
}