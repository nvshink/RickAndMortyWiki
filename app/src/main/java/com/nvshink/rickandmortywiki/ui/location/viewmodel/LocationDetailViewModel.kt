package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationDetailUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
open class LocationDetailViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {
    private val _reloadCounts = MutableStateFlow(0)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
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
    private val _uiState =
        MutableStateFlow<LocationDetailUiState>(LocationDetailUiState.LoadingState())
    val uiState = combine(
        _location,
        _contentType
    ) { location, contentType ->
        location.fold(
            onLoading = { data ->
                LocationDetailUiState.LoadingState(
                    location = data,
                    contentType = contentType
                )
            },
            onSuccess = { locationData ->
                LocationDetailUiState.ViewState(
                    location = locationData,
                    contentType = contentType
                )
            },
            onError = { message, exception ->
                LocationDetailUiState.ErrorState(
                    message = message,
                    exception = exception,
                    contentType = contentType
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

            is LocationDetailEvent.SetContentType ->
                _contentType.update { event.contentType }

            LocationDetailEvent.Refresh -> {
                reloadLocation()
            }
        }
    }

    private fun reloadLocation() {
        _reloadCounts.update { it + 1 }
    }
}