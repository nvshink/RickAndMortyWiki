package com.nvshink.rickandmortywiki.ui.episode.state

import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface EpisodeDetailUiState {
    val isLocal: Boolean
    val contentType: ContentType

    data class LoadingState(
        override val isLocal: Boolean = false,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : EpisodeDetailUiState

    data class ViewState(
        val episode: EpisodeModel,
        override val isLocal: Boolean,
        override val contentType: ContentType
    ) : EpisodeDetailUiState

    data class ErrorState(
        val error: Exception,
        override val isLocal: Boolean,
        override val contentType: ContentType
    ) : EpisodeDetailUiState
}