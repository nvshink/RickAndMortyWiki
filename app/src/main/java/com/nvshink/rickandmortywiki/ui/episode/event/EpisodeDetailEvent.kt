package com.nvshink.rickandmortywiki.ui.episode.event

import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface EpisodeDetailEvent {
    data class SetEpisode(val id: Int) : EpisodeDetailEvent
    data class SetContentType(val contentType: ContentType) : EpisodeDetailEvent
    data class SetIsLocal(val isLocal: Boolean) : EpisodeDetailEvent
    data object Refresh: EpisodeDetailEvent
}