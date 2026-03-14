package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationDetailUiState
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
open class LocationDetailViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {
    private val _reloadCounts = MutableStateFlow(0)
    private val _locationId = MutableStateFlow(0)

    private val _location =
        combine(
            _locationId,
            _reloadCounts
        ) { id, _ ->
            id
        }.flatMapLatest { id ->
            if (id <= 0) {
                flowOf(Resource.Loading())
            } else {
                repository.getLocationByIdApi(id).flatMapLatest { apiResult ->
                    if (apiResult is Resource.Error) {
                        repository.getLocationByIdDB(id)
                    } else {
                        flowOf(apiResult)
                    }
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading()
        )

    val uiState = _location.map{ location ->
        location.fold(
            onLoading = { data ->
                LocationDetailUiState.LoadingState(
                    location = data
                )
            },
            onSuccess = { locationData ->
                LocationDetailUiState.ViewState(
                    location = locationData
                )
            },
            onError = { message, exception ->
                LocationDetailUiState.ErrorState(
                    message = message,
                    exception = exception
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LocationDetailUiState.LoadingState()
    )

    fun onEvent(event: LocationDetailEvent) {
        when (event) {
            is LocationDetailEvent.SetLocation ->
                _locationId.update { event.id }

            LocationDetailEvent.Refresh -> {
                reloadLocation()
            }
        }
    }

    private fun reloadLocation() {
        _reloadCounts.update { it + 1 }
    }
}