package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationDetailUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
open class LocationDetailViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _reloadCounts = MutableStateFlow(0)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _locationId = MutableStateFlow(0)
    private val _isLocal = dataSourceManager.isLocal
    private val _isRefresh = MutableStateFlow(false)

    private val _location =
        combine(
            _locationId,
            _isLocal,
            _reloadCounts
        ) { locationId, isLocal, _ ->
            if (!isLocal) {
                repository.getLocationByIdApi(locationId)
            } else {
                repository.getLocationByIdDB(locationId)
            }
        }.flatMapLatest { it }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            Resource.Loading
        )
    private val _uiState =
        MutableStateFlow<LocationDetailUiState>(LocationDetailUiState.LoadingState())
    val uiState = combine(
        _uiState,
        _location,
        _contentType
    ) { uiState, location, contentType ->
        when (location) {
            is Resource.Loading -> {
                _uiState.update {
                    LocationDetailUiState.LoadingState(
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }

            is Resource.Success -> {
                _isRefresh.update { false }
                _uiState.update {
                    LocationDetailUiState.ViewState(
                        location = location.data,
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }

            is Resource.Error -> {
                _isRefresh.update { false }
                _uiState.update {
                    LocationDetailUiState.ErrorState(
                        error = location.exception,
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }
        }
        uiState
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        LocationDetailUiState.LoadingState()
    )

    fun onEvent(event: LocationDetailEvent) {
        when (event) {
            is LocationDetailEvent.SetLocation ->
                _locationId.update { event.id }

            is LocationDetailEvent.SetContentType ->
                _contentType.update { event.contentType }

            is LocationDetailEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

            LocationDetailEvent.Refresh -> {
                _isRefresh.update { true }
                reloadLocation()
            }
        }
    }
    private fun reloadLocation() {
        _reloadCounts.update { it + 1 }
    }
}