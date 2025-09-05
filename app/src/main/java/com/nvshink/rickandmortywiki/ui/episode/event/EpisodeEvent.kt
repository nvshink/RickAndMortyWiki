package com.nvshink.rickandmortywiki.ui.episode.event

sealed interface EpisodeEvent {
    data class SetUrls(val urls: List<String>): EpisodeEvent
}