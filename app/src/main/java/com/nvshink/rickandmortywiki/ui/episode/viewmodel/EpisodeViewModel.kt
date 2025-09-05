package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeUiState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeUiState.LoadingStateList
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeUiState.SuccessStateList
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeUiState.ErrorStateList
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
open class EpisodeViewModel @Inject constructor(
    private val repository: EpisodeRepository
) : ViewModel() {
    private val _urls = MutableStateFlow<List<String>>(emptyList())

    private val _loadedLocationsByUrls = _urls.flatMapLatest { urls ->
        repository.getEpisodesByIds(urls.map {
            it.substringAfterLast('/').toInt()
        })
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading
    )
    private val _uiStateSmallList =
        MutableStateFlow<EpisodeUiState>(LoadingStateList())

    val uiStateSmallList = combine(
        _uiStateSmallList,
        _loadedLocationsByUrls
    ) { uiStateSmallList, loadedLocationsByUrls ->
        when (loadedLocationsByUrls) {
            is Resource.Loading -> {
                _uiStateSmallList.update {
                    LoadingStateList(
                        episodesList = uiStateSmallList.episodesList,
                    )
                }
            }

            is Resource.Success -> {
                _uiStateSmallList.update {
                    SuccessStateList(
                        episodesList = loadedLocationsByUrls.data
                    )
                }
            }

            is Resource.Error -> {
                _uiStateSmallList.update {
                    ErrorStateList(
                        error = loadedLocationsByUrls.exception,
                        episodesList = uiStateSmallList.episodesList,
                    )
                }
            }
        }
        uiStateSmallList
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        LoadingStateList()
    )

    fun onEvent(event: EpisodeEvent) {
        when (event) {
            is EpisodeEvent.SetUrls -> _urls.update {
                event.urls
            }
        }
    }
}