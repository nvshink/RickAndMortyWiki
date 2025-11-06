package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.Resource
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.SuccessState
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
    private val repository: CharacterRepository
) : ViewModel() {
    private val _urls = MutableStateFlow<List<String>>(emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _characters = _urls.flatMapLatest { urls ->
        repository.getCharactersByIds(urls.map { it.substringAfterLast('/').toInt() })
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _uiState =
        MutableStateFlow<CharacterSmallListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _characters,
    ) { uiState, characters ->
        when (characters) {
            is Resource.Loading -> {
                _uiState.update {
                    LoadingState()
                }
            }

            is Resource.Success -> {
                _uiState.update {
                    SuccessState(
                        characterList = characters.data
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    ErrorState(
                        error = characters.exception
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
        }
    }
}