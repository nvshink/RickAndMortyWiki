package com.nvshink.rickandmortywiki.ui.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
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
open class CharacterPageListViewModel @Inject constructor(
    private val repository: CharacterRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _filter = MutableStateFlow(
        CharacterFilterModel(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )
    )
    private val _searchQuery = MutableStateFlow("")
    private val filter = combine(_filter, _searchQuery.debounce(1000L)) { filter, searchQuery ->
        filter.copy(name = searchQuery.ifBlank { null })
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

    private val _characters = filter.flatMapLatest { filter ->
        repository.getCharactersStream(filterModel = filter)
    }.cachedIn(viewModelScope)

    fun getCharacters(): Flow<PagingData<CharacterModel>> {
        return _characters
    }

    private val _uiState = MutableStateFlow(CharacterPageListUiState())

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
        CharacterPageListUiState()
    )

    fun onEvent(event: CharacterPageListEvent) {
        viewModelScope.launch {
            when (event) {

                is CharacterPageListEvent.SetFilter -> {
                    _filter.update {
                        event.filter
                    }
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
                }

                is CharacterPageListEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

                is CharacterPageListEvent.RetryPageLoad -> event.characters.retry()
            }
        }
    }
}