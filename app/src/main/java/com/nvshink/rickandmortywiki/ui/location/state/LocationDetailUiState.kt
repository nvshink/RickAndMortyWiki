package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel

interface LocationDetailUiState {

    data class LoadingState(
        val location: LocationModel? = null,
    ) : LocationDetailUiState

    data class ViewState(
        val location: LocationModel,
    ) : LocationDetailUiState

    data class ErrorState(
        val message: String? = null,
        val exception: Exception? = null,
    ) : LocationDetailUiState
}