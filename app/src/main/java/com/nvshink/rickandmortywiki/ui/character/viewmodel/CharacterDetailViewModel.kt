package com.nvshink.rickandmortywiki.ui.character.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: CharacterRepository
) : ViewModel() {
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _errorMessage = MutableStateFlow("")
    private val _characterId = MutableStateFlow(0)
    private val _character = _characterId.flatMapLatest {
        repository.getCharacterById(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        Resource.Loading
    )
    private val _uiState =
        MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.LoadingState())
    val uiState = combine(
        _uiState,
        _character,
        _errorMessage,
        _contentType
    ) { uiState, character, errorMessage, contentType ->
        when (character) {
            is Resource.Loading -> {
                _uiState.update {
                    CharacterDetailUiState.LoadingState(
                        character = null,
                        contentType = contentType
                    )
                }
            }

            is Resource.Success -> {
                _uiState.update {
                    CharacterDetailUiState.ViewState(
                        character = character.data,
                        contentType = contentType
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    CharacterDetailUiState.ErrorState(
                        errorMessage = errorMessage,
                        character = null,
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
            is CharacterDetailEvent.SetCharacter -> {
                _characterId.update {
                    event.id
                }
            }

            is CharacterDetailEvent.SetContentType -> {
                _contentType.update {
                    event.contentType
                }
            }
        }
    }

}