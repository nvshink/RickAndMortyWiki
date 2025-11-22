package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeDetailEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeDetailUiState
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
open class EpisodeDetailViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _reloadCounts = MutableStateFlow(0)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _episodeId = MutableStateFlow(0)
    private val _isLocal = dataSourceManager.isLocal
    private val _isRefresh = MutableStateFlow(false)

    private val _episode =
        combine(
            _episodeId,
            _isLocal,
            _reloadCounts
        ) { episodeId, isLocal, _ ->
            if (!isLocal) {
                repository.getEpisodeByIdApi(episodeId)
            } else {
                repository.getEpisodeByIdDB(episodeId)
            }
        }.flatMapLatest { it }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            Resource.Loading
        )
    private val _uiState =
        MutableStateFlow<EpisodeDetailUiState>(EpisodeDetailUiState.LoadingState())
    val uiState = combine(
        _uiState,
        _episode,
        _contentType
    ) { uiState, episode, contentType ->
        when (episode) {
            is Resource.Loading -> {
                _uiState.update {
                    EpisodeDetailUiState.LoadingState(
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }

            is Resource.Success -> {
                _isRefresh.update { false }
                _uiState.update {
                    EpisodeDetailUiState.ViewState(
                        episode = episode.data,
                        isLocal = _isLocal.value,
                        contentType = contentType
                    )
                }
            }

            is Resource.Error -> {
                _isRefresh.update { false }
                _uiState.update {
                    EpisodeDetailUiState.ErrorState(
                        error = episode.exception,
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
        EpisodeDetailUiState.LoadingState()
    )

    fun onEvent(event: EpisodeDetailEvent) {
        when (event) {
            is EpisodeDetailEvent.SetEpisode ->
                _episodeId.update { event.id }

            is EpisodeDetailEvent.SetContentType ->
                _contentType.update { event.contentType }

            is EpisodeDetailEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)

            EpisodeDetailEvent.Refresh -> {
                _isRefresh.update { true }
                reloadEpisode()
            }
        }
    }
    private fun reloadEpisode() {
        _reloadCounts.update { it + 1 }
    }
}