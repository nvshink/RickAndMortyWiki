package com.nvshink.rickandmortywiki.ui.episode.state

import com.nvshink.domain.episode.model.EpisodeModel

interface EpisodeUiState {
    val episodesList: List<EpisodeModel>


    data class LoadingStateList(
        override val episodesList: List<EpisodeModel> = emptyList(),
    ) : EpisodeUiState

    data class SuccessStateList(
        val isLocal: Boolean = false,
        override val episodesList: List<EpisodeModel> = emptyList(),
    ) : EpisodeUiState

    data class ErrorStateList(
        val error: Throwable? = null,
        override val episodesList: List<EpisodeModel> = emptyList(),
    ) : EpisodeUiState
}