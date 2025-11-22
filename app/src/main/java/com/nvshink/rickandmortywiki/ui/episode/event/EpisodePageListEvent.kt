package com.nvshink.rickandmortywiki.ui.episode.event

import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface EpisodePageListEvent {
    data object RefreshList : EpisodePageListEvent
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