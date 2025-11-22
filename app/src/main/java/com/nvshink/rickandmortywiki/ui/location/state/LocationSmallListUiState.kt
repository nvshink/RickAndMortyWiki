package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.utils.LocationSortFields
import com.nvshink.domain.resource.SortTypes

interface LocationSmallListUiState {
    val isLocal: Boolean

    data class LoadingState(
        override val isLocal: Boolean = false,
    ) : LocationSmallListUiState

    data class SuccessState(
        val locationList: List<LocationModel> = emptyList(),
        override val isLocal: Boolean = false,
    ) : LocationSmallListUiState

    data class ErrorState(
        val error: Exception? = null,
        override val isLocal: Boolean = false,
    ) : LocationSmallListUiState
}
