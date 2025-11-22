package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
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
open class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _reloadCounts = MutableStateFlow(0)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _characterId = MutableStateFlow(0)
    private val _isLocal = dataSourceManager.isLocal
    private val _isRefresh = MutableStateFlow(false)

    private val _character =
        combine(
            _characterId,
            _isLocal,
            _reloadCounts
        ) { characterId, isLocal, _ ->
            if (!isLocal) {
                repository.getCharacterByIdApi(characterId)
            } else {
                repository.getCharacterByIdDB(characterId)
            }
        }.flatMapLatest { it }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            Resource.Loading
        )
    private val _uiState =
        MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.LoadingState())
    val uiState = combine(
        _uiState,
        _character,
        _contentType
    ) { uiState, character, contentType ->
        when (character) {
            is Resource.Loading -> {
                _uiState.update {
                    CharacterDetailUiState.LoadingState(
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }

            is Resource.Success -> {
                _isRefresh.update { false }
                _uiState.update {
                    CharacterDetailUiState.ViewState(
                        character = character.data,
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }

            is Resource.Error -> {
                _isRefresh.update { false }
                _uiState.update {
                    CharacterDetailUiState.ErrorState(
                        error = character.exception,
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
        CharacterDetailUiState.LoadingState()
    )

    fun onEvent(event: CharacterDetailEvent) {
        when (event) {
            is CharacterDetailEvent.SetCharacter ->
                _characterId.update { event.id }

            is CharacterDetailEvent.SetContentType ->
                _contentType.update { event.contentType }

            is CharacterDetailEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

            CharacterDetailEvent.Refresh -> {
                _isRefresh.update { true }
                reloadCharacter()
            }
        }
    }
    private fun reloadCharacter() {
        _reloadCounts.update { it + 1 }
    }
}