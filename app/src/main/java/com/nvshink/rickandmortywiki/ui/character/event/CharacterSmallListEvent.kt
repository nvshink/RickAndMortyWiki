package com.nvshink.rickandmortywiki.ui.character.event


sealed interface CharacterSmallListEvent {
    data class SetUrls (val urls: List<String>): CharacterSmallListEvent

}