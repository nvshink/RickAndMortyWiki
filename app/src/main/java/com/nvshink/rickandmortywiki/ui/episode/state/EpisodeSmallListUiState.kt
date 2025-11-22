package com.nvshink.rickandmortywiki.ui.episode.state

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState

interface EpisodeSmallListUiState {
    val isLocal: Boolean

    data class LoadingState(
        override val isLocal: Boolean = false,
    ) : EpisodeSmallListUiState

    data class SuccessState(
        val episodeList: List<EpisodeModel>,
        override val isLocal: Boolean
    ) : EpisodeSmallListUiState

    data class ErrorState(
        val error: Exception?,
        override val isLocal: Boolean
    ) : EpisodeSmallListUiState
}