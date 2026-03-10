package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeSmallListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState.ErrorState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState.LoadingState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState.SuccessState
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
class EpisodeSmallListViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    private val dataSourceManager: DataSourceManager
) : ViewModel() {
    private val _isLocal = dataSourceManager.isLocal
    private val _reloadCounts = MutableStateFlow(0)
    private val _urls = MutableStateFlow<List<String>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _episodes = combine(_urls, _reloadCounts) { urls, _ ->
        urls.map { it.substringAfterLast('/').toInt() }
    }.flatMapLatest { ids ->
        repository.getEpisodesByIdsApi(ids).flatMapLatest { apiResult ->
            if (apiResult is Resource.Error) {
                repository.getEpisodesByIdsDB(ids)
            } else {
                flowOf(apiResult)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    val uiState = combine(_episodes, _isLocal) { episodes, isLocal ->
        episodes.fold(
            onLoading = { _ ->
                LoadingState(isLocal = isLocal)
            },
            onSuccess = { episodeList ->
                SuccessState(
                    episodeList = episodeList,
                    isLocal = isLocal
                )
            },
            onError = { _, exception ->
                ErrorState(
                    error = exception,
                    isLocal = isLocal
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoadingState()
    )

    fun onEvent(event: EpisodeSmallListEvent) {
        when (event) {
            is EpisodeSmallListEvent.SetUrls -> _urls.update { event.urls }
            is EpisodeSmallListEvent.Refresh -> _reloadCounts.update { it + 1 }
        }
    }
}
