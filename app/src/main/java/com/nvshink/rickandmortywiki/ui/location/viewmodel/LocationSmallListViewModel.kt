package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.location.event.LocationSmallListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.location.state.LocationSmallListUiState.SuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LocationSmallListViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _isLocal = dataSourceManager.isLocal
    private val _urls = MutableStateFlow<List<String>>(emptyList())
    private val _reloadCounts = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _locations = combine(_urls, _isLocal, _reloadCounts) { urls, isLocal, _ ->
        val ids = urls.map { it.substringAfterLast('/').toInt() }
        if(!isLocal) {
            repository.getLocationsByIdsApi(ids = ids)
        } else {
            repository.getLocationsByIdsDB(ids = ids)
        }
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _uiState =
        MutableStateFlow<LocationSmallListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _locations,
    ) { uiState, locations ->
        when (locations) {
            is Resource.Loading -> {
                _uiState.update {
                    LoadingState(
                        isLocal = _isLocal.value
                    )
                }
            }

            is Resource.Success -> {
                _uiState.update {
                    SuccessState(
                        locationList = locations.data,
                        isLocal = _isLocal.value
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    ErrorState(
                        error = locations.exception,
                        isLocal = _isLocal.value
                    )
                }
            }
        }
        uiState
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        LoadingState()
    )

    fun onEvent(event: LocationSmallListEvent) {
        when (event) {
            is LocationSmallListEvent.SetUrls -> _urls.update { event.urls }
            is LocationSmallListEvent.Refresh -> _reloadCounts.update { it + 1 }
        }
    }
}