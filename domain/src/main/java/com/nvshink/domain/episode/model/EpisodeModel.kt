package com.nvshink.domain.episode.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 * @param id The id of the episode.
 * @param name The name of the episode.
 * @param airDate The air date of the episode.
 * @param episode The code of the episode.
 * @param characters List of characters who have been seen in the episode.
 * @param url Link to the episode's own endpoint.
 * @param created Time at which the episode was created in the database.
 */
data class EpisodeModel(
    val id:	Int,
    val name: String,
    val airDate: LocalDate,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: ZonedDateTime
)
