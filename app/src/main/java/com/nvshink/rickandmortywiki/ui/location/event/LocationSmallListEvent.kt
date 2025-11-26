package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.domain.resource.SortTypes

sealed interface LocationSmallListEvent {
    data class SetUrls (val urls: List<String>): LocationSmallListEvent
    data object Refresh: LocationSmallListEvent
}