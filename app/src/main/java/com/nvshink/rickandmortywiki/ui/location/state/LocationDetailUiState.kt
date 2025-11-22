package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import java.time.ZonedDateTime

interface LocationDetailUiState {
    val isLocal: Boolean
    val contentType: ContentType

    data class LoadingState(
        override val isLocal: Boolean = false,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationDetailUiState

    data class ViewState(
        val location: LocationModel,
        override val isLocal: Boolean,
        override val contentType: ContentType
    ) : LocationDetailUiState

    data class ErrorState(
        val error: Exception,
        override val isLocal: Boolean,
        override val contentType: ContentType
    ) : LocationDetailUiState
}