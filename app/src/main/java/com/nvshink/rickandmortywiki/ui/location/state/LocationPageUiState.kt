package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.utils.LocationSortFields
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface LocationPageUiState {
    val locationList: List<LocationModel>
    val currentLocation: LocationModel?
    val filter: LocationFilterModel
    val isShowingList: Boolean
    val isShowingFilter: Boolean
    val isSortDropDownExpanded: Boolean
    val isAtTop: Boolean
    val isRefreshing: Boolean
    val searchBarText: String
    val searchBarFiltersText: String
    val sortType: SortTypes
    val sortFields: LocationSortFields
    val contentType: ContentType

    data class LoadingState(
        override val locationList: List<LocationModel> = emptyList(),
        override val currentLocation: LocationModel? = null,
        override val filter: LocationFilterModel = LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        ),
        override val isShowingList: Boolean = true,
        override val isShowingFilter: Boolean = false,
        override val isSortDropDownExpanded: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: LocationSortFields = LocationSortFields.NAME,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationPageUiState

    data class SuccessState(
        override val locationList: List<LocationModel> = emptyList(),
        override val currentLocation: LocationModel? = null,
        override val filter: LocationFilterModel = LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        ),
        override val isShowingList: Boolean = true,
        override val isShowingFilter: Boolean = false,
        override val isSortDropDownExpanded: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: LocationSortFields = LocationSortFields.NAME,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationPageUiState

    data class ErrorState(
        val error: Throwable? = null,
        override val locationList: List<LocationModel> = emptyList(),
        override val currentLocation: LocationModel? = null,
        override val filter: LocationFilterModel = LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        ),
        override val isShowingList: Boolean = true,
        override val isShowingFilter: Boolean = false,
        override val isSortDropDownExpanded: Boolean = false,
        override val isAtTop: Boolean = true,
        override val isRefreshing: Boolean = false,
        override val searchBarText: String = "",
        override val searchBarFiltersText: String = "",
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: LocationSortFields = LocationSortFields.NAME,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationPageUiState

}