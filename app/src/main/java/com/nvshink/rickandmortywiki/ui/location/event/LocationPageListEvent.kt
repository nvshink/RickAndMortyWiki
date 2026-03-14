package com.nvshink.rickandmortywiki.ui.location.event

import androidx.paging.compose.LazyPagingItems
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface LocationPageListEvent {
    data class RefreshList(val locations: LazyPagingItems<LocationModel>) : LocationPageListEvent
    data class RetryPageLoad(val locations: LazyPagingItems<LocationModel>) : LocationPageListEvent
    data object ShowFilterDialog : LocationPageListEvent
    data object HideFilterDialog : LocationPageListEvent
    data class SetFilter(val filter: LocationFilterModel) : LocationPageListEvent
    data class SetUiStateFilter(val filter: LocationFilterModel) : LocationPageListEvent
    data object ClearFilterUi : LocationPageListEvent
    data object LoadMore : LocationPageListEvent
    data class SetContentType(val contentType: ContentType) : LocationPageListEvent
    data class SetSearchBarText(val text: String) : LocationPageListEvent
}