package com.nvshink.rickandmortywiki.ui.episode.event

import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent

interface EpisodeSmallListEvent {
    data class SetUrls (val urls: List<String>): EpisodeSmallListEvent
}