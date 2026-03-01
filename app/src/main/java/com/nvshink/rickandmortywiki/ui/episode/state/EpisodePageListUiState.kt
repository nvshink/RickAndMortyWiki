package com.nvshink.rickandmortywiki.ui.episode.state

import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.rickandmortywiki.ui.generic.state.PageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface EpisodePageListUiState : PageListUiState<EpisodeModel> {
    val episodeList: List<EpisodeModel>
    val currentEpisode: EpisodeModel?
    val filter: EpisodeFilterModel

    data class LoadingState(
        override val episodeList: List<EpisodeModel> = emptyList(),
        override val currentEpisode: EpisodeModel? = null,
        override val filter: EpisodeFilterModel = EpisodeFilterModel(
            name = null,
            episode = null
        ),
        override val isShowingFilter: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val isLocal: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val contentType: ContentType = ContentType.LIST_ONLY,
        override val error: Throwable? = null
    ) : EpisodePageListUiState

    data class SuccessState(
        override val episodeList: List<EpisodeModel>,
        override val currentEpisode: EpisodeModel?,
        override val filter: EpisodeFilterModel,
        override val isShowingFilter: Boolean,
        override val isAtTop: Boolean,
        override val isRefreshing: Boolean,
        override val isLocal: Boolean,
        override val searchBarText: String,
        override val searchBarFiltersText: String,
        override val contentType: ContentType,
        override val error: Throwable? = null
    ) : EpisodePageListUiState

    data class ErrorState(
        override val error: Exception?,
        override val episodeList: List<EpisodeModel>,
        override val currentEpisode: EpisodeModel? = null,
        override val filter: EpisodeFilterModel,
        override val isShowingFilter: Boolean,
        override val isAtTop: Boolean,
        override val isRefreshing: Boolean,
        override val isLocal: Boolean,
        override val searchBarText: String,
        override val searchBarFiltersText: String,
        override val contentType: ContentType
    ) : EpisodePageListUiState
}