package com.nvshink.rickandmortywiki.ui.character.event

import com.nvshink.rickandmortywiki.ui.location.event.LocationSmallListEvent


sealed interface CharacterSmallListEvent {
    data class SetUrls (val urls: List<String>): CharacterSmallListEvent
    data object Refresh: CharacterSmallListEvent

}