package com.nvshink.rickandmortywiki.ui.episode.viewmodel


import android.util.Log
import androidx.compose.animation.SharedTransitionScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState.*
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

    private val _reloadCounts = MutableStateFlow(0)
    private val _isRefresh = MutableStateFlow(false)
    private val _contentType = MutableStateFlow(ContentType.LIST_ONLY)
    private val _pageInfoModel = MutableStateFlow(PageInfoModel(next = null, prev = null))
    private val _isLocal = dataSourceManager.isLocal
    private val _filter = MutableStateFlow(
        EpisodeFilterModel(
            name = null,
            episode = null
        )
    )
    private val _isInitialise = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _searchQueryDebounce = _searchQuery.asStateFlow().debounce(1000L)
    private val filter = combine(_filter, _searchQueryDebounce) { filter, searchQueryDebounce ->
        if (_isInitialise.value) {
            _isRefresh.update { true }
            _pageInfoModel.update { PageInfoModel(next = null, prev = null) }
        } else {
            _isInitialise.update { true }
        }
        filter.copy(name = searchQueryDebounce.ifBlank { null })
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        EpisodeFilterModel(
            name = null,
            episode = null
        )
    )

    private val _newEpisodes = combine(
        filter,
        _reloadCounts,
        _isLocal
    ) { filter, _, isLocal ->
        if (!isLocal) {
            repository.getEpisodesApi(
                pageInfoModel = _pageInfoModel.value,
                filterModel = filter
            ).flatMapLatest { response ->
                flow {
                    _pageInfoModel.update { response.first }
                    emit(response.second)
                }
            }
        } else {
            repository.getEpisodesDB(filterModel = filter)
        }
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading
        )

    private val _oldEpisodes = MutableStateFlow<List<EpisodeModel>>(emptyList())

    private val _episodes = _newEpisodes.flatMapLatest { newEpisodes ->
        flow {
            val episodes = when (newEpisodes) {
                Resource.Loading -> newEpisodes
                is Resource.Success -> {
                    if (_isRefresh.value) {
                        _isRefresh.update { false }
                        _oldEpisodes.update { newEpisodes.data }
                        newEpisodes
                    } else {
                        val updatedEpisodes =
                            (_oldEpisodes.value + newEpisodes.data).associateBy { it.id }.values.toList()
                        _oldEpisodes.update { updatedEpisodes }
                        Resource.Success(updatedEpisodes)
                    }
                }

                is Resource.Error -> newEpisodes
            }
            emit(episodes)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading
    )

    private val _uiState =
        MutableStateFlow<EpisodePageListUiState>(LoadingState())

    val uiState = combine(
        _uiState,
        _episodes,
        _contentType
    ) { uiState, episodes, contentType ->
        when (episodes) {
            is Resource.Loading -> {
                _uiState.update {
                    LoadingState(
                        episodeList = uiState.episodeList,
                        currentEpisode = uiState.currentEpisode,
                        contentType = contentType,
                        filter = uiState.filter,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isLocal = _isLocal.value,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                        isRefreshing = _isRefresh.value
                    )
                }
            }

            is Resource.Success -> {
                var episodeList = if (uiState.isRefreshing) {
                    _isRefresh.update { false }
                    episodes.data
                } else {
                    (uiState.episodeList + episodes.data).associateBy { it.id }.values.toList()
                }
                _uiState.update {
                    SuccessState(
                        episodeList = episodeList,
                        currentEpisode = uiState.currentEpisode,
                        filter = uiState.filter,
                        contentType = contentType,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isRefreshing = _isRefresh.value,
                        isLocal = _isLocal.value,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                    )
                }
            }

            is Resource.Error -> {
                _isRefresh.update { false }
                _uiState.update {
                    ErrorState(
                        error = episodes.exception,
                        episodeList = if (uiState.isRefreshing) emptyList() else uiState.episodeList,
                        currentEpisode = uiState.currentEpisode,
                        filter = uiState.filter,
                        contentType = contentType,
                        isShowingFilter = uiState.isShowingFilter,
                        isAtTop = uiState.isAtTop,
                        isLocal = _isLocal.value,
                        searchBarText = uiState.searchBarText,
                        searchBarFiltersText = uiState.searchBarFiltersText,
                        isRefreshing = _isRefresh.value
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

    fun onEvent(event: EpisodePageListEvent) {
        when (event) {

            EpisodePageListEvent.LoadMore -> reloadEpisodes()

            is EpisodePageListEvent.SetFilter -> {
                _filter.update {
                    event.filter
                }
            }

            is EpisodePageListEvent.SetUiStateFilter -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(filter = event.filter)
                        is LoadingState -> it.copy(filter = event.filter)
                        is ErrorState -> it.copy(filter = event.filter)
                        else -> it
                    }
                }
            }

            EpisodePageListEvent.ClearFilterUi -> {
                val emptyFilter = EpisodeFilterModel(
                    name = null,
                    episode = null
                )
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(filter = emptyFilter)
                        is LoadingState -> it.copy(filter = emptyFilter)
                        is ErrorState -> it.copy(filter = emptyFilter)
                        else -> it
                    }
                }
            }

            EpisodePageListEvent.ShowFilterDialog -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingFilter = true, filter = _filter.value)
                        is LoadingState -> it.copy(isShowingFilter = true, filter = _filter.value)
                        is ErrorState -> it.copy(isShowingFilter = true, filter = _filter.value)
                        else -> it
                    }
                }
            }

            EpisodePageListEvent.HideFilterDialog -> {
                _uiState.update {
                    when (it) {
                        is SuccessState -> it.copy(isShowingFilter = false)
                        is LoadingState -> it.copy(isShowingFilter = false)
                        is ErrorState -> it.copy(isShowingFilter = false)
                        else -> it
                    }
                }
            }

            EpisodePageListEvent.RefreshList -> {
                _pageInfoModel.update { PageInfoModel(next = null, prev = null) }
                _isRefresh.update { true }
                reloadEpisodes()
            }

            is EpisodePageListEvent.SetContentType -> _contentType.update { event.contentType }
            is EpisodePageListEvent.SetSearchBarText -> {
                _uiState.update {
                    when (it) {
                        is LoadingState -> it.copy(searchBarText = event.text)
                        is SuccessState -> it.copy(searchBarText = event.text)
                        is ErrorState -> it.copy(searchBarText = event.text)
                        else -> it
                    }
                }
                _searchQuery.update { event.text }
            }

            is EpisodePageListEvent.SetIsLocal -> dataSourceManager.setLocal(event.isLocal)
        }
    }

    private fun reloadEpisodes() {
        _reloadCounts.update { it + 1 }
    }
}