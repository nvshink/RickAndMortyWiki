package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.TerminalSeparatorType
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
import com.nvshink.rickandmortywiki.ui.episode.model.EpisodeUiModel
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
open class EpisodePageListViewModel @Inject constructor(
    private val repository: EpisodeRepository
) : ViewModel() {
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
    }.map { pagingData ->
        pagingData.map {
            EpisodeUiModel.Episode(it) }
            .insertSeparators(terminalSeparatorType = TerminalSeparatorType.SOURCE_COMPLETE) { before: EpisodeUiModel.Episode?, after: EpisodeUiModel.Episode? ->
                val beforeSeason = before?.data?.episode?.substringBefore("E")
                val afterSeason = after?.data?.episode?.substringBefore("E")
                if (afterSeason != null && beforeSeason != afterSeason) {
                    EpisodeUiModel.Header(afterSeason)
                } else {
                    null
                }
            }
    }.cachedIn(viewModelScope)

    fun getEpisodes(): Flow<PagingData<EpisodeUiModel>> {
        return _episodes
    }

    private val _uiState = MutableStateFlow(EpisodePageListUiState())

    val uiState = _uiState.stateIn(
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

                is EpisodePageListEvent.SetSearchBarText -> {
                    _uiState.update {
                        it.copy(searchBarText = event.text)
                    }
                    _searchQuery.update { event.text }
                }
                is EpisodePageListEvent.RetryPageLoad -> event.episodes.retry()
            }
        }
    }
}
