package com.nvshink.rickandmortywiki.ui.location.event

interface LocationDetailEvent {
    data class SetLocation(val id: Int) : LocationDetailEvent
    data object Refresh: LocationDetailEvent
}