package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.character.model.CharacterFilterModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
open class CharacterPageListViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {
    private val _isRefresh = MutableStateFlow(false)
    private val _isLoadMore = MutableStateFlow(false)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _pageInfoModel = MutableStateFlow<PageInfoModel?>(null)
    private val _filter = MutableStateFlow(
        CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )
    )
    private val filter = _filter.asStateFlow().debounce(1000L)

    private val _characters = combine(
        filter,
        _isLoadMore,
        _isRefresh
    ) { filter, _, _ ->
        _isLoadMore.update { false }
        _isRefresh.update { false }
        repository.getCharacters(pageInfoModel = _pageInfoModel.value, filterModel = filter)
    }.flatMapLatest { flow -> flow }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _uiState =
        MutableStateFlow<CharacterPageListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _characters,
        _contentType
    ) { uiState, characters, contentType ->
        when (characters) {
            is Resource.Loading -> {
                _uiState.update {
                    LoadingState(
                        characterList = uiState.characterList,
                        currentCharacter = uiState.currentCharacter,
                        isShowingList = uiState.isShowingList,
                        contentType = contentType,
                        filter = uiState.filter,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                    )
                }
            }

            is Resource.Success -> {
                val newCharacterList = characters.data.second
                var characterList =
                    (uiState.characterList + newCharacterList).associateBy { it.id }.values.toList()
                _pageInfoModel.update { characters.data.first }
                _uiState.update {
                    SuccessState(
                        isLocal = characters.isLocal,
                        characterList = characterList,
                        currentCharacter = uiState.currentCharacter,
                        filter = uiState.filter,
                        isShowingList = uiState.isShowingList,
                        contentType = contentType,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isRefreshing = false,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    ErrorState(
                        error = characters.exception,
                        characterList = uiState.characterList,
                        currentCharacter = uiState.currentCharacter,
                        filter = uiState.filter,
                        isShowingList = uiState.isShowingList,
                        contentType = contentType,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
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

    fun onEvent(event: CharacterPageListEvent) {
        when (event) {
            is CharacterPageListEvent.UpdateCurrentCharacter -> {
                _uiState.update {
                    when (it) {
                        is LoadingState -> it.copy(currentCharacter = event.character)
                        is SuccessState -> it.copy(currentCharacter = event.character)
                        is ErrorState -> it.copy(currentCharacter = event.character)
                        else -> it
                    }
                }
            }

            CharacterPageListEvent.LoadMore -> _isLoadMore.update { true }
            CharacterPageListEvent.ClearFilterSelection -> {
                _filter.update {
                    CharacterFilterModel(
                        name = null,
                        status = null,
                        species = null,
                        type = null,
                        gender = null
                    )
                }
            }

            is CharacterPageListEvent.SetFilter -> {
                _pageInfoModel.update { null }
                _filter.update {
                    event.filter
                }
//                _uiState.update {
//                    LoadingState(
//                        currentCharacter = it.currentCharacter,
//                        filter = it.filter,
//                        isShowingList = it.isShowingList,
//                        isShowingFilter = it.isShowingFilter,
//                        isAtTop = it.isAtTop,
//                        isRefreshing = it.isRefreshing,
//                        searchBarText = it.searchBarText,
//                        searchBarFiltersText = it.searchBarFiltersText,
//                        contentType = it.contentType
//                    )
//                }
            }

            is CharacterPageListEvent.SetUiStateFilter -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(filter = event.filter)
                        is LoadingState -> it.copy(filter = event.filter)
                        is ErrorState -> it.copy(filter = event.filter)
                        else -> it
                    }

                }
            }

            CharacterPageListEvent.ShowFilterDialog -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingFilter = true)
                        is LoadingState -> it.copy(isShowingFilter = true)
                        is ErrorState -> it.copy(isShowingFilter = true)
                        else -> it
                    }
                }
            }

            CharacterPageListEvent.HideFilterDialog -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingFilter = false)
                        is LoadingState -> it.copy(isShowingFilter = false)
                        is ErrorState -> it.copy(isShowingFilter = false)
                        else -> it
                    }
                }
            }

            CharacterPageListEvent.HideList -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingList = false)
                        is LoadingState -> it.copy(isShowingList = false)
                        is ErrorState -> it.copy(isShowingList = false)
                        else -> it
                    }
                }
            }

            CharacterPageListEvent.ShowList -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingList = true)
                        is LoadingState -> it.copy(isShowingList = true)
                        is ErrorState -> it.copy(isShowingList = true)
                        else -> it
                    }
                }
            }

            CharacterPageListEvent.RefreshList -> {
                _pageInfoModel.update { null }
                _isRefresh.update { true }
                _uiState.update { LoadingState(isRefreshing = true, filter = it.filter) }
            }

            is CharacterPageListEvent.SetContentType -> _contentType.update { event.contentType }
            is CharacterPageListEvent.SetSearchBarText -> _uiState.update {
                when (it) {
                    is LoadingState -> it.copy(searchBarText = event.text)
                    is SuccessState -> it.copy(searchBarText = event.text)
                    is ErrorState -> it.copy(searchBarText = event.text)
                    else -> it
                }
            }
        }
    }
}