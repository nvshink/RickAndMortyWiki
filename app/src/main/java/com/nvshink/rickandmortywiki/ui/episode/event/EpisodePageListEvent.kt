package com.nvshink.rickandmortywiki.ui.episode.event

import androidx.paging.compose.LazyPagingItems
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.rickandmortywiki.ui.episode.model.EpisodeUiModel

sealed interface EpisodePageListEvent {
    data class RefreshList(val episodes: LazyPagingItems<EpisodeUiModel>) : EpisodePageListEvent
    data class RetryPageLoad(val episodes: LazyPagingItems<EpisodeUiModel>) : EpisodePageListEvent
    data object ShowFilterDialog : EpisodePageListEvent
    data object HideFilterDialog : EpisodePageListEvent
    data class SetFilter(val filter: EpisodeFilterModel) : EpisodePageListEvent
    data class SetUiStateFilter(val filter: EpisodeFilterModel) : EpisodePageListEvent
    data object ClearFilterUi : EpisodePageListEvent
    data class SetSearchBarText(val text: String) : EpisodePageListEvent
}
