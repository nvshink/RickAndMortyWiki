package com.nvshink.rickandmortywiki.ui.episode.state

import androidx.paging.PagingData
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.rickandmortywiki.ui.generic.state.PageListUiState

data class EpisodePageListUiState(
    val episodeList: PagingData<EpisodeModel> = PagingData.empty(),
    val currentEpisode: EpisodeModel? = null,
    val filter: EpisodeFilterModel = EpisodeFilterModel(
        name = null,
        episode = null
    ),
    override val isShowingFilter: Boolean = false,
    override val isAtTop: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLocal: Boolean = false,
    override val searchBarText: String = "",
    override val searchBarFiltersText: String = "",
    override val error: Exception? = null
) : PageListUiState<EpisodeModel>
