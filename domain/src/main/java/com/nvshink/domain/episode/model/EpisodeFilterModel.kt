package com.nvshink.domain.episode.model

/**
 * This data class is used to store information about which filtering options have been selected. If option is null, it means that it is not selected.
 */
data class EpisodeFilterModel(
    val name: String?,
    val episode: String?
)
