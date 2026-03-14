package com.nvshink.rickandmortywiki.ui.generic.state

interface PageListUiState<T : Any> {
    val isShowingFilter: Boolean
    val isAtTop: Boolean
    val isRefreshing: Boolean
    val searchBarText: String
    val searchBarFiltersText: String
    val error: Throwable?
}
