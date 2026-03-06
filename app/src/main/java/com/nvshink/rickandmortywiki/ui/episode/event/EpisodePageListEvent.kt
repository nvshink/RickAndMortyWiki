package com.nvshink.rickandmortywiki.ui.episode.event

import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

sealed interface EpisodePageListEvent {
    data class RefreshList(val episodes: LazyPagingItems<EpisodeModel>) : EpisodePageListEvent
    data class RetryPageLoad(val episodes: LazyPagingItems<EpisodeModel>) : EpisodePageListEvent
    data object ShowFilterDialog : EpisodePageListEvent
    data object HideFilterDialog : EpisodePageListEvent
    data class SetFilter(val filter: EpisodeFilterModel) : EpisodePageListEvent
    data class SetUiStateFilter(val filter: EpisodeFilterModel) : EpisodePageListEvent
    data object ClearFilterUi : EpisodePageListEvent
    data object LoadMore : EpisodePageListEvent
    data class SetContentType(val contentType: ContentType) : EpisodePageListEvent
    data class SetSearchBarText(val text: String) : EpisodePageListEvent
    data class SetIsLocal(val isLocal: Boolean) : EpisodePageListEvent

}