package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.ui.generic.state.PageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface LocationPageListUiState : PageListUiState<LocationModel> {
    val locationList: List<LocationModel>
    val currentLocation: LocationModel?
    val filter: LocationFilterModel

    data class LoadingState(
        override val locationList: List<LocationModel> = emptyList(),
        override val currentLocation: LocationModel? = null,
        override val filter: LocationFilterModel = LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        ),
        override val isShowingFilter: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val isLocal: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val contentType: ContentType = ContentType.LIST_ONLY,
        override val error: Throwable? = null,
    ) : LocationPageListUiState

    data class SuccessState(
        override val locationList: List<LocationModel>,
        override val currentLocation: LocationModel?,
        override val filter: LocationFilterModel,
        override val isShowingFilter: Boolean,
        override val isAtTop: Boolean,
        override val isRefreshing: Boolean,
        override val isLocal: Boolean,
        override val searchBarText: String,
        override val searchBarFiltersText: String,
        override val contentType: ContentType,
        override val error: Throwable? = null
    ) : LocationPageListUiState


    data class ErrorState(
        override val error: Exception?,
        override val locationList: List<LocationModel>,
        override val currentLocation: LocationModel? = null,
        override val filter: LocationFilterModel,
        override val isShowingFilter: Boolean,
        override val isAtTop: Boolean,
        override val isRefreshing: Boolean,
        override val isLocal: Boolean,
        override val searchBarText: String,
        override val searchBarFiltersText: String,
        override val contentType: ContentType
    ) : LocationPageListUiState

}