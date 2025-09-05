package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageUiState
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageUiState.SuccessState
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageUiState.ErrorState
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
open class LocationPageListViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {
    private val _urls = MutableStateFlow<List<String>>(emptyList())

    private val _loadedLocationsByUrls = _urls.flatMapLatest { urls ->
        repository.getLocationsByIds(urls.map {
            it.substringAfterLast('/').toInt()
        })
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading
    )
    private val _uiStateSmallList =
        MutableStateFlow<LocationPageUiState>(LoadingState())

    val uiStateSmallList = combine(
        _uiStateSmallList,
        _loadedLocationsByUrls
    ) { uiStateSmallList, loadedLocationsByUrls ->
        when (loadedLocationsByUrls) {
            is Resource.Loading -> {
                _uiStateSmallList.update {
                    LoadingState(
                        locationList = uiStateSmallList.locationList,
                    )
                }
            }

            is Resource.Success -> {
                _uiStateSmallList.update {
                    SuccessState(
                        locationList = loadedLocationsByUrls.data
                    )
                }
            }

            is Resource.Error -> {
                _uiStateSmallList.update {
                    ErrorState(
                        error = loadedLocationsByUrls.exception,
                        locationList = uiStateSmallList.locationList,
                    )
                }
            }
        }
        uiStateSmallList
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        LoadingState()
    )

    fun onEvent(event: LocationPageListEvent) {
        when (event) {
            LocationPageListEvent.ClearFilterSelection -> TODO()
            LocationPageListEvent.HideFilterDialog -> TODO()
            LocationPageListEvent.HideList -> TODO()
            LocationPageListEvent.HideSortDropDown -> TODO()
            LocationPageListEvent.HideToTopButton -> TODO()
            LocationPageListEvent.LoadMore -> TODO()
            LocationPageListEvent.RefreshList -> TODO()
            is LocationPageListEvent.SetContentType -> TODO()
            is LocationPageListEvent.SetFilter -> TODO()
            is LocationPageListEvent.SetSearchBarText -> TODO()
            is LocationPageListEvent.SetSortFields -> TODO()
            is LocationPageListEvent.SetSortType -> TODO()
            is LocationPageListEvent.SetUiStateFilter -> TODO()
            LocationPageListEvent.ShowFilterDialog -> TODO()
            LocationPageListEvent.ShowList -> TODO()
            LocationPageListEvent.ShowSortDropDown -> TODO()
            LocationPageListEvent.ShowToTopButton -> TODO()
            is LocationPageListEvent.UpdateCurrentLocation -> TODO()
        }
    }
}