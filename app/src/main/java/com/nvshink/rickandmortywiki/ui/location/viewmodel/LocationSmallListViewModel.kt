package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.location.utils.LocationSortFields
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
    private val repository: LocationRepository
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortTypes.ASCENDING)
    private val _sortFields = MutableStateFlow(LocationSortFields.NAME)
    private val _urls = MutableStateFlow<List<String>>(emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _locations = _urls.flatMapLatest { urls ->
        repository.getLocationsByIds(urls.map { it.substringAfterLast('=').toInt() })
    }
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
        _sortType,
        _sortFields,
    ) { uiState, locations, sortType, sortFields ->
        when (locations) {
            is Resource.Loading -> {
                _uiState.update {
                    LoadingState(
                        sortType = sortType,
                        sortFields = sortFields
                    )
                }
            }

            is Resource.Success -> {
                var locationList = when (sortType) {
                    SortTypes.ASCENDING ->
                        when (sortFields) {
                            LocationSortFields.NAME -> locations.data.sortedBy { it.name }
                            LocationSortFields.CREATED -> locations.data.sortedBy { it.created }
                            LocationSortFields.TYPE -> locations.data.sortedBy { it.type }
                        }

                    SortTypes.DESCENDING ->
                        when (sortFields) {
                            LocationSortFields.NAME -> locations.data.sortedByDescending { it.name }
                            LocationSortFields.CREATED -> locations.data.sortedByDescending { it.created }
                            LocationSortFields.TYPE -> locations.data.sortedByDescending { it.type }
                        }

                    SortTypes.NONE -> locations.data
                }
                _uiState.update {
                    SuccessState(
                        locationList = locationList,
                        sortType = sortType,
                        sortFields = sortFields,
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    ErrorState(
                        error = locations.exception,
                        sortType = sortType,
                        sortFields = sortFields,
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
            is LocationSmallListEvent.SetSortFields -> _sortFields.update { event.sortFields }

            is LocationSmallListEvent.SetSortType -> _sortType.update { event.sortType }

            is LocationSmallListEvent.SetUrls -> _urls.update { event.urls }
        }
    }
}