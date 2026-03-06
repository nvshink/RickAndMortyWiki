package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState
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
open class EpisodePageListViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _filter = MutableStateFlow(
        EpisodeFilterModel(
            name = null,
            episode = null
        )
    )
    private val _searchQuery = MutableStateFlow("")
    private val filter = combine(_filter, _searchQuery.debounce(1000L)) { filter, searchQuery ->
        filter.copy(name = searchQuery.ifBlank { null })
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        EpisodeFilterModel(
            name = null,
            episode = null
        )
    )

    private val _episodes = filter.flatMapLatest { filter ->
        repository.getEpisodesStream(filterModel = filter)
    }.cachedIn(viewModelScope)

    fun getEpisodes(): Flow<PagingData<EpisodeModel>> {
        return _episodes
    }

    private val _uiState = MutableStateFlow(EpisodePageListUiState())

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
        EpisodePageListUiState()
    )

    fun onEvent(event: EpisodePageListEvent) {
        viewModelScope.launch {
            when (event) {

                is EpisodePageListEvent.SetFilter -> {
                    _filter.update {
                        event.filter
                    }
                }

                is EpisodePageListEvent.SetUiStateFilter -> {
                    _uiState.update {
                        it.copy(filter = event.filter)
                    }
                }

                EpisodePageListEvent.ClearFilterUi -> {
                    val emptyFilter = EpisodeFilterModel(
                        name = null,
                        episode = null
                    )
                    _filter.update { emptyFilter }
                    _uiState.update {
                        it.copy(filter = emptyFilter)
                    }
                }

                EpisodePageListEvent.ShowFilterDialog -> {
                    _uiState.update {
                        it.copy(
                            isShowingFilter = true,
                            filter = _filter.value
                        )
                    }
                }

                EpisodePageListEvent.HideFilterDialog -> {
                    _uiState.update {
                        it.copy(isShowingFilter = false)
                    }
                }

                is EpisodePageListEvent.RefreshList -> event.episodes.refresh()

                is EpisodePageListEvent.SetContentType -> _contentType.update { event.contentType }

                is EpisodePageListEvent.SetSearchBarText -> {
                    _uiState.update {
                        it.copy(searchBarText = event.text)
                    }
                    _searchQuery.update { event.text }
                }

                is EpisodePageListEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

                is EpisodePageListEvent.RetryPageLoad -> event.episodes.retry()

                EpisodePageListEvent.LoadMore -> {}
            }
        }
    }
}
