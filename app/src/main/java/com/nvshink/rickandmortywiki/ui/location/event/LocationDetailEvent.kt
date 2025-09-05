package com.nvshink.rickandmortywiki.ui.location.event

import com.nvshink.rickandmortywiki.ui.utils.ContentType

interface LocationDetailEvent {
    data class SetLocation(val id: Int) : LocationDetailEvent
    data class SetContentType(val contentType: ContentType) : LocationDetailEvent
}