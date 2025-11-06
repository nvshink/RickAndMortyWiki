package com.nvshink.rickandmortywiki.ui.character.event

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.SortTypes

sealed interface CharacterSmallListEvent {
    data class SetUrls (val urls: List<String>): CharacterSmallListEvent

}