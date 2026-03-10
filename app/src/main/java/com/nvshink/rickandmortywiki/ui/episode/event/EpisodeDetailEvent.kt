package com.nvshink.rickandmortywiki.ui.episode.event

sealed interface EpisodeDetailEvent {
    data class SetEpisode(val id: Int) : EpisodeDetailEvent
    data class SetIsLocal(val isLocal: Boolean) : EpisodeDetailEvent
    data object Refresh: EpisodeDetailEvent
}