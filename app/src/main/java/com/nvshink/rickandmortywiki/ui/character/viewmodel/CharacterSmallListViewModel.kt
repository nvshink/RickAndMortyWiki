package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.SuccessState
import com.nvshink.rickandmortywiki.ui.location.event.LocationSmallListEvent
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
class CharacterSmallListViewModel @Inject constructor(
    private val repository: CharacterRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _isLocal = dataSourceManager.isLocal
    private val _reloadCounts = MutableStateFlow(0)
    private val _urls = MutableStateFlow<List<String>>(emptyList())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _characters = combine(_urls, _isLocal, _reloadCounts) { urls, isLocal, _ ->
        val ids = urls.map { it.substringAfterLast('/').toInt() }
        if(!isLocal) {
            repository.getCharactersByIdsApi(ids = ids)
        } else {
            repository.getCharactersByIdsDB(ids = ids)
        }
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _uiState =
        MutableStateFlow<CharacterSmallListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _characters
    ) { uiState, characters ->
        when (characters) {
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
                        characterList = characters.data,
                        isLocal = _isLocal.value
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    ErrorState(
                        error = characters.exception,
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

    fun onEvent(event: CharacterSmallListEvent) {
        when (event) {
            is CharacterSmallListEvent.SetUrls -> _urls.update { event.urls }
            is CharacterSmallListEvent.Refresh -> _reloadCounts.update { it + 1 }
        }
    }
}