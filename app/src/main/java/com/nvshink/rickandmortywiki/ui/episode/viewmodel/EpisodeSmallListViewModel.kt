package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterSmallListEvent
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeSmallListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState.SuccessState
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
class EpisodeSmallListViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _isLocal = dataSourceManager.isLocal
    private val _reloadCounts = MutableStateFlow(0)

    private val _urls = MutableStateFlow<List<String>>(emptyList())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _episodes = combine(_urls, _isLocal, _reloadCounts) { urls, isLocal, _ ->
        val ids = urls.map { it.substringAfterLast('/').toInt() }
        if(!isLocal) {
            repository.getEpisodesByIdsApi(ids = ids)
        } else {
            repository.getEpisodesByIdsDB(ids = ids)
        }
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _uiState =
        MutableStateFlow<EpisodeSmallListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _episodes,
    ) { uiState, episodes ->
        when (episodes) {
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
                        episodeList = episodes.data,
                        isLocal = _isLocal.value
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update {
                    ErrorState(
                        error = episodes.exception,
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

    fun onEvent(event: EpisodeSmallListEvent) {
        when (event) {
            is EpisodeSmallListEvent.SetUrls -> _urls.update { event.urls }
            is EpisodeSmallListEvent.Refresh -> _reloadCounts.update { it + 1 }
        }
    }
}