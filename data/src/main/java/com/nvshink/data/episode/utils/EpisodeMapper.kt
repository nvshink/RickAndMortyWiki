package com.nvshink.data.episode.utils

import com.nvshink.data.episode.local.entity.EpisodeEntity
import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.domain.episode.model.EpisodeModel
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val airDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

fun EpisodeEntity.toModel(): EpisodeModel = EpisodeModel(
    id = id,
    name = name,
    airDate = LocalDate.parse(airDate, airDateFormatter),
    episode = episode,
    characters = characters,
    url = url,
    created = ZonedDateTime.parse(created)
)

fun EpisodeModel.toEntity(): EpisodeEntity = EpisodeEntity(
    id = id,
    name = name,
    airDate = airDate.format(airDateFormatter),
    episode = episode,
    characters = characters,
    url = url,
    created = created.toString()
)

fun EpisodeResponse.toModel(): EpisodeModel = EpisodeModel(
    id = id,
    name = name,
    airDate = LocalDate.parse(airDate, airDateFormatter),
    episode = episode,
    characters = characters,
    url = url,
    created = ZonedDateTime.parse(created)
)

fun EpisodeModel.toResponse(): EpisodeResponse = EpisodeResponse(
    id = id,
    name = name,
    airDate = airDate.format(airDateFormatter),
    episode = episode,
    characters = characters,
    url = url,
    created = created.toString()
)

fun EpisodeEntity.toResponse(): EpisodeResponse = EpisodeResponse(
    id = id,
    name = name,
    airDate = airDate,
    episode = episode,
    characters = characters,
    url = url,
    created = created
)

fun EpisodeResponse.toEntity(): EpisodeEntity = EpisodeEntity(
    id = id,
    name = name,
    airDate = airDate,
    episode = episode,
    characters = characters,
    url = url,
    created = created
)
