package com.nvshink.rickandmortywiki.ui.location.event

sealed interface LocationSmallListEvent {
    data class SetUrls (val urls: List<String>): LocationSmallListEvent
    data object Refresh: LocationSmallListEvent
}