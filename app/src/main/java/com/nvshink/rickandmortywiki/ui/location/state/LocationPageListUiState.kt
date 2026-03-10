package com.nvshink.rickandmortywiki.ui.location.state

import androidx.paging.PagingData
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.ui.generic.state.PageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType

data class LocationPageListUiState(
    val locationList: PagingData<LocationModel> = PagingData.empty(),
    val currentLocation: LocationModel? = null,
    val filter: LocationFilterModel = LocationFilterModel(
        name = null,
        type = null,
        dimension = null
    ),
    override val isShowingFilter: Boolean = false,
    override val isAtTop: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLocal: Boolean = false,
    override val searchBarText: String = "",
    override val searchBarFiltersText: String = "",
    override val error: Exception? = null
) : PageListUiState<LocationModel>
