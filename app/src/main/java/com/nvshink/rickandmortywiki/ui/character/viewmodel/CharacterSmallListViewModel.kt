package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState.SuccessState
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

@HiltViewModel
class CharacterSmallListViewModel @Inject constructor(
    private val repository: CharacterRepository,
) : ViewModel() {
    private val _reloadCounts = MutableStateFlow(0)
    private val _urls = MutableStateFlow<List<String>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _characters = combine(_urls, _reloadCounts) { urls, _ ->
        urls.map { it.substringAfterLast('/').toInt() }
    }.flatMapLatest { ids ->
        repository.getCharactersByIdsApi(ids).flatMapLatest { apiResult ->
            if (apiResult is Resource.Error) {
                repository.getCharactersByIdsDB(ids)
            } else {
                flowOf(apiResult)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    val uiState = _characters.map { characters ->
        characters.fold(
            onLoading = { _ ->
                LoadingState
            },
            onSuccess = { characterList ->
                SuccessState(
                    characterList = characterList
                )
            },
            onError = { message, exception ->
                ErrorState(
                    message = message,
                    exception = exception,
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoadingState
    )

    fun onEvent(event: CharacterSmallListEvent) {
        when (event) {
            is CharacterSmallListEvent.SetUrls -> _urls.update { event.urls }
            is CharacterSmallListEvent.Refresh -> _reloadCounts.update { it + 1 }
        }
    }
}
