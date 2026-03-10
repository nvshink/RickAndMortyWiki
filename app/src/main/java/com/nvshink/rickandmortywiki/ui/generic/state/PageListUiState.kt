package com.nvshink.rickandmortywiki.ui.generic.state

import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface PageListUiState<T : Any> {
    val isShowingFilter: Boolean
    val isAtTop: Boolean
    val isRefreshing: Boolean
    val isLocal: Boolean
    val searchBarText: String
    val searchBarFiltersText: String
    val error: Throwable?
}
