package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import java.time.ZonedDateTime

interface LocationDetailUiState {
    val location: LocationModel?
    val contentType: ContentType

    data class LoadingState(
        override val location: LocationModel? = null,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationDetailUiState

    data class ViewState(
        override val location: LocationModel = LocationModel(
            id = 0,
            name = "",
            type = "",
            dimension = "",
            residents = emptyList(),
            url = "",
            created = ZonedDateTime.now()
        ),
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationDetailUiState

    data class ErrorState(
        val errorMessage: String = "",
        override val location: LocationModel? = null,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationDetailUiState
}