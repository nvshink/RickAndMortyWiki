package com.nvshink.rickandmortywiki.ui.episode.event

interface EpisodeSmallListEvent {
    data class SetUrls (val urls: List<String>): EpisodeSmallListEvent
    data object Refresh: EpisodeSmallListEvent

}