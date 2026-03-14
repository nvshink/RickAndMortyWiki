package com.nvshink.rickandmortywiki.ui.character.event

sealed interface CharacterDetailEvent {
    data class SetCharacter(val id: Int) : CharacterDetailEvent
    data class SetIsLocal(val isLocal: Boolean) : CharacterDetailEvent
    data object Refresh: CharacterDetailEvent
}