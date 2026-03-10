package com.nvshink.rickandmortywiki.ui.location.state

import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import java.time.ZonedDateTime

interface LocationDetailUiState {
    val contentType: ContentType

    data class LoadingState(
        val location: LocationModel? = null,
        override val contentType: ContentType = ContentType.LIST_ONLY
    ) : LocationDetailUiState

    data class ViewState(
        val location: LocationModel,
        override val contentType: ContentType
    ) : LocationDetailUiState

    data class ErrorState(
        val message: String? = null,
        val exception: Exception? = null,
        override val contentType: ContentType
    ) : LocationDetailUiState
}