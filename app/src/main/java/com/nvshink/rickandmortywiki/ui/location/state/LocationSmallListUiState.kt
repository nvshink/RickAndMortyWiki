package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel

interface LocationSmallListUiState {
    val isLocal: Boolean

    data class LoadingState(
        override val isLocal: Boolean = false,
    ) : LocationSmallListUiState

    data class SuccessState(
        val locationList: List<LocationModel>,
        override val isLocal: Boolean,
    ) : LocationSmallListUiState

    data class ErrorState(
        val error: Exception?,
        override val isLocal: Boolean,
    ) : LocationSmallListUiState
}
