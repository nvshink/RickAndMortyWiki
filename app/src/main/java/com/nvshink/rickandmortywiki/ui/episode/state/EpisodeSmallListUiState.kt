package com.nvshink.rickandmortywiki.ui.episode.state

import com.nvshink.domain.episode.model.EpisodeModel

interface EpisodeSmallListUiState {

    data object LoadingState : EpisodeSmallListUiState

    data class SuccessState(
        val episodeList: List<EpisodeModel>,
    ) : EpisodeSmallListUiState

    data class ErrorState(
        val error: Exception?,
    ) : EpisodeSmallListUiState
}