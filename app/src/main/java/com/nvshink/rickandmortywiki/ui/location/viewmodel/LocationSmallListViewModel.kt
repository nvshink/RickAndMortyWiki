package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.location.event.LocationSmallListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState.SuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LocationSmallListViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _isLocal = dataSourceManager.isLocal
    private val _reloadCounts = MutableStateFlow(0)
    private val _urls = MutableStateFlow<List<String>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _locations = combine(_urls, _reloadCounts) { urls, _ ->
        urls.map { it.substringAfterLast('/').toInt() }
    }.flatMapLatest { ids ->
        repository.getLocationsByIdsApi(ids).flatMapLatest { apiResult ->
            if (apiResult is Resource.Error) {
                repository.getLocationsByIdsDB(ids)
            } else {
                flowOf(apiResult)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    val uiState = combine(_locations, _isLocal) { locations, isLocal ->
        locations.fold(
            onLoading = { _ ->
                LoadingState(isLocal = isLocal)
            },
            onSuccess = { locationList ->
                SuccessState(
                    locationList = locationList,
                    isLocal = isLocal
                )
            },
            onError = { _, exception ->
                ErrorState(
                    error = exception,
                    isLocal = isLocal
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoadingState()
    )

    fun onEvent(event: LocationSmallListEvent) {
        when (event) {
            is LocationSmallListEvent.SetUrls -> _urls.update { event.urls }
            is LocationSmallListEvent.Refresh -> _reloadCounts.update { it + 1 }
        }
    }
}
