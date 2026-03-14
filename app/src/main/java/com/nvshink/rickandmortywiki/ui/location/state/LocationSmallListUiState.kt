package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel

interface LocationSmallListUiState {

    data object LoadingState : LocationSmallListUiState

    data class SuccessState(
        val locationList: List<LocationModel>,
    ) : LocationSmallListUiState

    data class ErrorState(
        val error: Exception?,
    ) : LocationSmallListUiState
}
