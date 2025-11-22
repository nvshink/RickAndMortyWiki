package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface LocationPageListEvent {
    data object RefreshList : LocationPageListEvent
    data object ShowFilterDialog : LocationPageListEvent
    data object HideFilterDialog : LocationPageListEvent
    data class SetFilter(val filter: LocationFilterModel) : LocationPageListEvent
    data class SetUiStateFilter(val filter: LocationFilterModel) : LocationPageListEvent
    data object ClearFilterUi : LocationPageListEvent
    data object LoadMore : LocationPageListEvent
    data class SetContentType(val contentType: ContentType) : LocationPageListEvent
    data class SetSearchBarText(val text: String) : LocationPageListEvent
    data class SetIsLocal(val isLocal: Boolean) : LocationPageListEvent
}