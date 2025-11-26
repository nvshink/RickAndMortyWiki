package com.nvshink.rickandmortywiki.ui.episode.event

import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.location.event.LocationSmallListEvent

interface EpisodeSmallListEvent {
    data class SetUrls (val urls: List<String>): EpisodeSmallListEvent
    data object Refresh: EpisodeSmallListEvent

}