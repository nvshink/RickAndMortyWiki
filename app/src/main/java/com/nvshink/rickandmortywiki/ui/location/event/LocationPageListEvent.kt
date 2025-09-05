package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.utils.LocationSortFields
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface LocationPageListEvent {
    data object ShowList : LocationPageListEvent
    data object HideList : LocationPageListEvent
    data object RefreshList: LocationPageListEvent
    data object ShowSortDropDown: LocationPageListEvent
    data object HideSortDropDown: LocationPageListEvent
    data object ShowFilterDialog: LocationPageListEvent
    data object HideFilterDialog: LocationPageListEvent
    data object ShowToTopButton: LocationPageListEvent
    data object HideToTopButton: LocationPageListEvent
    data object ClearFilterSelection: LocationPageListEvent
    data class SetFilter(val filter: LocationFilterModel): LocationPageListEvent
    data class SetUiStateFilter(val filter: LocationFilterModel): LocationPageListEvent
    data object LoadMore: LocationPageListEvent
    data class UpdateCurrentLocation(val location: LocationModel?) : LocationPageListEvent
    data class SetSortType(val sortType: SortTypes) : LocationPageListEvent
    data class SetSortFields(val sortFields: LocationSortFields) : LocationPageListEvent
    data class SetContentType(val contentType: ContentType) : LocationPageListEvent
    data class SetSearchBarText (val text: String): LocationPageListEvent
}