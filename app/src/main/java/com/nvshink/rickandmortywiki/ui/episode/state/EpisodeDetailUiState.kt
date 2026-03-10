package com.nvshink.rickandmortywiki.ui.episode.state

import com.nvshink.domain.episode.model.EpisodeModel

interface EpisodeDetailUiState {

    data class LoadingState(
        val episode: EpisodeModel? = null,
    ) : EpisodeDetailUiState

    data class ViewState(
        val episode: EpisodeModel,
    ) : EpisodeDetailUiState

    data class ErrorState(
        val message: String? = null,
        val exception: Exception? = null,
    ) : EpisodeDetailUiState
}
