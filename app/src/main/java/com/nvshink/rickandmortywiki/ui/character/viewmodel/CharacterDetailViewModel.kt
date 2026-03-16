package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
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
open class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _savedStateHandle = savedStateHandle
    private val _reloadCounts = MutableStateFlow(0)
    private val _characterId = MutableStateFlow(_savedStateHandle.get<Int>("characterId") ?: 0)
    private val _character = combine(
        _characterId,
        _reloadCounts
    ) { characterId, _ ->
        characterId
    }.flatMapLatest { characterId ->
        if (characterId <= 0) {
            flowOf(Resource.Loading())
        } else {
            repository.getCharacterByIdApi(characterId).flatMapLatest { apiResult ->
                if (apiResult is Resource.Error) {
                    repository.getCharacterByIdDB(characterId)
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

    val uiState = _character.map { character ->
        character.fold(
            onLoading = { data ->
                CharacterDetailUiState.LoadingState(
                    character = data
                )
            },
            onSuccess = { characterData ->
                CharacterDetailUiState.ViewState(
                    character = characterData
                )
            },
            onError = { message, exception ->
                CharacterDetailUiState.ErrorState(
                    message = message,
                    exception = exception
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CharacterDetailUiState.LoadingState()
    )
    fun onEvent(event: CharacterDetailEvent) {
        when (event) {
            is CharacterDetailEvent.SetCharacter -> {
                _characterId.update { event.id }
                _savedStateHandle["characterId"] = event.id
            }

            CharacterDetailEvent.Refresh -> {
                reloadCharacter()
            }
        }
    }

    private fun reloadCharacter() {
        _reloadCounts.update { it + 1 }
    }
}
