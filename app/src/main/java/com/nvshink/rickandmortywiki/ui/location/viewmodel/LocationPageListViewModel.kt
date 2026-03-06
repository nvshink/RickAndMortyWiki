package com.nvshink.rickandmortywiki.ui.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
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
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _filter = MutableStateFlow(
        LocationFilterModel(
            name = null,
            type = null,
            dimension = null
        )
    )
    private val _searchQuery = MutableStateFlow("")
    private val filter = combine(_filter, _searchQuery.debounce(1000L)) { filter, searchQuery ->
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

    private val _locations = filter.flatMapLatest { filter ->
        repository.getLocationsStream(filterModel = filter)
    }.cachedIn(viewModelScope)

    fun getLocations(): Flow<PagingData<LocationModel>> {
        return _locations
    }

    private val _uiState = MutableStateFlow(LocationPageListUiState())

    val uiState = combine(
        _uiState,
        _contentType,
    ) { uiState, contentType ->
        uiState.copy(
            contentType = contentType,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LocationPageListUiState()
    )

    fun onEvent(event: LocationPageListEvent) {
        viewModelScope.launch {
            when (event) {

                is LocationPageListEvent.SetFilter -> {
                    _filter.update {
                        event.filter
                    }
                }

                is LocationPageListEvent.SetUiStateFilter -> {
                    _uiState.update {
                        it.copy(filter = event.filter)
                    }
                }

                LocationPageListEvent.ClearFilterUi -> {
                    val emptyFilter = LocationFilterModel(
                        name = null,
                        type = null,
                        dimension = null
                    )
                    _filter.update { emptyFilter }
                    _uiState.update {
                        it.copy(filter = emptyFilter)
                    }
                }

                LocationPageListEvent.ShowFilterDialog -> {
                    _uiState.update {
                        it.copy(
                            isShowingFilter = true,
                            filter = _filter.value
                        )
                    }
                }

                LocationPageListEvent.HideFilterDialog -> {
                    _uiState.update {
                        it.copy(isShowingFilter = false)
                    }
                }

                is LocationPageListEvent.RefreshList -> event.locations.refresh()

                is LocationPageListEvent.SetContentType -> _contentType.update { event.contentType }

                is LocationPageListEvent.SetSearchBarText -> {
                    _uiState.update {
                        it.copy(searchBarText = event.text)
                    }
                    _searchQuery.update { event.text }
                }

                is LocationPageListEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

                is LocationPageListEvent.RetryPageLoad -> event.locations.retry()

                LocationPageListEvent.LoadMore -> {}
            }
        }
    }
}
