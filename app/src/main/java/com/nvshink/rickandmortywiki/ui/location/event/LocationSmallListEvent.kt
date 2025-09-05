package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.domain.location.utils.LocationSortFields
import com.nvshink.domain.resource.SortTypes

interface LocationSmallListEvent {
    data class SetSortType(val sortType: SortTypes) : LocationSmallListEvent
    data class SetSortFields(val sortFields: LocationSortFields) : LocationSmallListEvent
    data class SetUrls (val urls: List<String>): LocationSmallListEvent
}