package com.nvshink.rickandmortywiki.ui.location.viewmodel

import android.util.Log
import androidx.compose.animation.SharedTransitionScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageListUiState
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageListUiState.*
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
open class LocationPageListViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {

    private val _reloadCounts = MutableStateFlow(0)
    private val _isRefresh = MutableStateFlow(false)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _pageInfoModel = MutableStateFlow(PageInfoModel(next = null, prev = null))
    private val pageInfoModel = combine(_pageInfoModel, _isRefresh){ pageInfoModel, isRefresh ->
        if (isRefresh) {
            PageInfoModel(next = null, prev = null)
        } else {
            pageInfoModel
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PageInfoModel(next = null, prev = null))
    private val _isLocal = dataSourceManager.isLocal
    private val _filter = MutableStateFlow(
        LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        )
    )
    private val _searchQuery = MutableStateFlow("")
    private val searchQuery = _searchQuery.asStateFlow().debounce(1000L)
    private val filter = combine(_filter, searchQuery) { filter, searchQuery ->
        _isRefresh.update { true }
        filter.copy(name = searchQuery.ifBlank { null })
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        )
    )

    private val _locations = combine(
        filter,
        _reloadCounts,
        _isLocal
    ) { filter, _, isLocal ->
        if (!isLocal) {
            repository.getLocationsApi(
                pageInfoModel = pageInfoModel.value,
                filterModel = filter
            ).flatMapLatest { response ->
                flow {
                    _pageInfoModel.update { response.first }
                    emit(response.second)
                }
            }
        } else {
            repository.getLocationsDB(filterModel = filter)
        }
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _uiState =
        MutableStateFlow<LocationPageListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _locations,
        _contentType
    ) { uiState, locations, contentType ->
        when (locations) {
            is Resource.Loading -> {
                _uiState.update {
                    LoadingState(
                        locationList = uiState.locationList,
                        currentLocation = uiState.currentLocation,
                        contentType = contentType,
                        filter = uiState.filter,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isLocal = _isLocal.value,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                        isRefreshing = _isRefresh.value
                    )
                }
            }

            is Resource.Success -> {
                val newLocationList = locations.data
                var locationList = if (uiState.isRefreshing) {
                    _isRefresh.update { false }
                    newLocationList
                } else {
                    (uiState.locationList + newLocationList).associateBy { it.id }.values.toList()
                }
                _uiState.update {
                    SuccessState(
                        locationList = locationList,
                        currentLocation = uiState.currentLocation,
                        filter = uiState.filter,
                        contentType = contentType,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isRefreshing = _isRefresh.value,
                        isLocal = _isLocal.value,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                    )
                }
            }

            is Resource.Error -> {
                _isRefresh.update { false }
                _uiState.update {
                    ErrorState(
                        error = locations.exception,
                        locationList = if (uiState.isRefreshing) emptyList() else uiState.locationList,
                        currentLocation = uiState.currentLocation,
                        filter = uiState.filter,
                        contentType = contentType,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isLocal = _isLocal.value,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                        isRefreshing = _isRefresh.value
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

    fun onEvent(event: LocationPageListEvent) {
        when (event) {

            LocationPageListEvent.LoadMore -> reloadLocations()

            is LocationPageListEvent.SetFilter -> {
                _filter.update {
                    event.filter
                }
            }

            is LocationPageListEvent.SetUiStateFilter -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(filter = event.filter)
                        is LoadingState -> it.copy(filter = event.filter)
                        is ErrorState -> it.copy(filter = event.filter)
                        else -> it
                    }
                }
            }

            LocationPageListEvent.ClearFilterUi -> {
                val emptyFilter = LocationFilterModel(
                    name = null,
                    type = null,
                    dimension = null
                )
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(filter = emptyFilter)
                        is LoadingState -> it.copy(filter = emptyFilter)
                        is ErrorState -> it.copy(filter = emptyFilter)
                        else -> it
                    }
                }
            }

            LocationPageListEvent.ShowFilterDialog -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingFilter = true, filter = _filter.value)
                        is LoadingState -> it.copy(isShowingFilter = true, filter = _filter.value)
                        is ErrorState -> it.copy(isShowingFilter = true, filter = _filter.value)
                        else -> it
                    }
                }
            }

            LocationPageListEvent.HideFilterDialog -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingFilter = false)
                        is LoadingState -> it.copy(isShowingFilter = false)
                        is ErrorState -> it.copy(isShowingFilter = false)
                        else -> it
                    }
                }
            }

            LocationPageListEvent.RefreshList -> {
                _isRefresh.update { true }
                reloadLocations()
            }

            is LocationPageListEvent.SetContentType -> _contentType.update { event.contentType }
            is LocationPageListEvent.SetSearchBarText -> {
                _uiState.update {
                    when (it) {
                        is LoadingState -> it.copy(searchBarText = event.text)
                        is SuccessState -> it.copy(searchBarText = event.text)
                        is ErrorState -> it.copy(searchBarText = event.text)
                        else -> it
                    }
                }
                _searchQuery.update { event.text }
            }

            is LocationPageListEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)
        }
    }

    private fun reloadLocations() {
        _reloadCounts.update { it + 1 }
    }
}