package com.nvshink.rickandmortywiki.ui.episode.event

sealed interface EpisodeDetailEvent {
    data class SetEpisode(val id: Int) : EpisodeDetailEvent
    data object Refresh: EpisodeDetailEvent
}