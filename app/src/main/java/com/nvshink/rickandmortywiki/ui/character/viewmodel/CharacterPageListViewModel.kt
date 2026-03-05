package com.nvshink.rickandmortywiki.ui.character.viewmodel

import android.util.Log
import androidx.compose.animation.SharedTransitionScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.flatMap
import com.nvshink.data.character.paging.CharacterPagingSource
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState.*
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
open class CharacterPageListViewModel @Inject constructor(
    private val repository: CharacterRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {

    init{ viewModelScope.launch { repository.getCount() } }

    private val _refreshTrigger = MutableStateFlow(0)
    private val _isRefreshing = MutableStateFlow(false)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _isLocal = dataSourceManager.isLocal
    private val _filter = MutableStateFlow(
        CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )
    )
    private val _isInitialise = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _searchQueryDebounce = _searchQuery.asStateFlow().debounce(1000L)

    private val filter = combine(_filter, _searchQueryDebounce) { filter, searchQueryDebounce ->
//        if(_isInitialise.value) {
////            _isRefreshing.update { true }
//        } else {_isInitialise.update { true }}
        filter.copy(name = searchQueryDebounce.ifBlank { null })
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )
    )

    private val _characters = combine(
        filter,
        _refreshTrigger,
        _isLocal
    ) { filter, trigger, isLocal ->
        Triple(filter, trigger, isLocal)
    }.flatMapLatest { (filter, trigger, isLocal) ->
//        _isRefreshing.update { true }
        if (isLocal) {
            flow {
//                _isRefreshing.update { false }
                emit(PagingData.empty())
            }
        } else {
            repository.getCharactersStream(filterModel = filter, coroutineScope = viewModelScope).also {
//                viewModelScope.launch {
//                    _isRefreshing.update { false }
//                }
            }
        }
    }

    fun getCharacters(): Flow<PagingData<CharacterModel>> {
        return _characters
    }

    private val _uiState = MutableStateFlow(CharacterPageListUiState())

    val uiState = combine(
        _uiState,
        _contentType,
//        _isRefreshing
    ) { uiState, contentType,->
        uiState.copy(
            contentType = contentType,
//            isRefreshing = isRefreshing
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CharacterPageListUiState()
    )

    fun onEvent(event: CharacterPageListEvent) {
        viewModelScope.launch {
            when (event) {

                CharacterPageListEvent.LoadMore -> { /* Paging loads automatically */ }

                is CharacterPageListEvent.SetFilter -> {
                    _filter.update {
                        event.filter
                    }
                    _refreshTrigger.update { it + 1 }
                }

                is CharacterPageListEvent.SetUiStateFilter -> {
                    _uiState.update {
                        it.copy(filter = event.filter)
                    }
                }

                CharacterPageListEvent.ClearFilterUi -> {
                    val emptyFilter = CharacterFilterModel(
                        name = null,
                        status = null,
                        species = null,
                        type = null,
                        gender = null
                    )
                    _filter.update { emptyFilter }
                    _uiState.update {
                        it.copy(filter = emptyFilter)
                    }
                    _refreshTrigger.update { it + 1 }
                }

                CharacterPageListEvent.ShowFilterDialog -> {
                    _uiState.update {
                        it.copy(
                                isShowingFilter = true,
                                filter = _filter.value
                            )
                    }
                }

                CharacterPageListEvent.HideFilterDialog -> {
                    _uiState.update {
                        it.copy(isShowingFilter = false)
                    }
                }

                is CharacterPageListEvent.RefreshList -> event.characters.refresh()

                is CharacterPageListEvent.SetContentType -> _contentType.update { event.contentType }

                is CharacterPageListEvent.SetSearchBarText -> {
                    _uiState.update {
                        it.copy(searchBarText = event.text)
                    }
                    _searchQuery.update { event.text }
                    _refreshTrigger.update { it + 1 }
                }

                is CharacterPageListEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

                is CharacterPageListEvent.RetryPageLoad -> event.characters.retry()
            }
        }
    }
}