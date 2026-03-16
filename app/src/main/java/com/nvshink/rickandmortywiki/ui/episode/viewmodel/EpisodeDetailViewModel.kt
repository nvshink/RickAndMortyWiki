package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeDetailEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeDetailUiState
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
open class EpisodeDetailViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _savedStateHandle = savedStateHandle
    private val _reloadCounts = MutableStateFlow(0)
    private val _episodeId = MutableStateFlow(_savedStateHandle.get<Int>("episodeId") ?: 0)

    private val _episode =
        combine(
            _episodeId,
            _reloadCounts
        ) { episodeId, _ ->
            episodeId
        }.flatMapLatest { characterId ->
            if (characterId <= 0) {
                flowOf(Resource.Loading())
            } else {
                repository.getEpisodeByIdApi(characterId).flatMapLatest { apiResult ->
                    if (apiResult is Resource.Error) {
                        repository.getEpisodeByIdDB(characterId)
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
    val uiState = _episode.map { episode ->
        episode.fold(
            onLoading = { data ->
                EpisodeDetailUiState.LoadingState(
                    episode = data
                )
            },
            onSuccess = { data ->
                EpisodeDetailUiState.ViewState(
                    episode = data
                )
            },
            onError = { message, exception ->
                EpisodeDetailUiState.ErrorState(
                    message = message,
                    exception = exception
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EpisodeDetailUiState.LoadingState()
    )

    fun onEvent(event: EpisodeDetailEvent) {
        when (event) {
            is EpisodeDetailEvent.SetEpisode -> {
                _episodeId.update { event.id }
                _savedStateHandle["episodeId"] = event.id
            }

            EpisodeDetailEvent.Refresh -> {
                reloadEpisode()
            }
        }
    }

    private fun reloadEpisode() {
        _reloadCounts.update { it + 1 }
    }
}
