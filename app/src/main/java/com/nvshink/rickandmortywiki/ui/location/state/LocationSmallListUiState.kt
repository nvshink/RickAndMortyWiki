package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.utils.LocationSortFields
import com.nvshink.domain.resource.SortTypes

interface LocationSmallListUiState {
    val sortType: SortTypes
    val sortFields: LocationSortFields

    data class LoadingState(
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: LocationSortFields = LocationSortFields.NAME
    ) : LocationSmallListUiState

    data class SuccessState(
        val locationList: List<LocationModel> = emptyList(),
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: LocationSortFields = LocationSortFields.NAME
    ) : LocationSmallListUiState

    data class ErrorState(
        val error: Throwable? = null,
        override val sortType: SortTypes = SortTypes.ASCENDING,
        override val sortFields: LocationSortFields = LocationSortFields.NAME
    ) : LocationSmallListUiState
}
