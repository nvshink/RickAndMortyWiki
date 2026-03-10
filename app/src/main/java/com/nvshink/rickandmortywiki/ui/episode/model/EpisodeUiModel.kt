package com.nvshink.rickandmortywiki.ui.episode.model

import com.nvshink.domain.episode.model.EpisodeModel

sealed class EpisodeUiModel {
    data class Episode(val data: EpisodeModel) : EpisodeUiModel()
    data class Header(val season: String) : EpisodeUiModel()
}
