package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.domain.resource.SortTypes

interface LocationSmallListEvent {
    data class SetUrls (val urls: List<String>): LocationSmallListEvent
}