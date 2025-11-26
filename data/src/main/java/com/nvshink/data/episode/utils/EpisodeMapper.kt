package com.nvshink.data.episode.utils

import com.nvshink.data.episode.local.entity.EpisodeEntity
import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.domain.episode.model.EpisodeModel
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object EpisodeMapper {
    fun entityToModel(entity: EpisodeEntity): EpisodeModel = EpisodeModel(
        id = entity.id,
        name = entity.name,
        airDate = LocalDate.parse(entity.airDate, DateTimeFormatter.ofPattern("MMMM d, yyyy")),
        episode = entity.episode,
        characters = entity.characters,
        url = entity.url,
        created = ZonedDateTime.parse(entity.created)
    )
    fun modelToEntity(model: EpisodeModel): EpisodeEntity = EpisodeEntity(
        id = model.id,
        name = model.name,
        airDate = model.airDate.toString(),
        episode = model.episode,
        characters = model.characters,
        url = model.url,
        created = model.created.toString()
    )

    fun responseToModel(response: EpisodeResponse): EpisodeModel = EpisodeModel(
        id = response.id,
        name = response.name,
        airDate = LocalDate.parse(response.airDate, DateTimeFormatter.ofPattern("MMMM d, yyyy")),
        episode = response.episode,
        characters = response.characters,
        url = response.url,
        created = ZonedDateTime.parse(response.created)
    )
    fun modelToResponse(model: EpisodeModel): EpisodeResponse = EpisodeResponse(
        id = model.id,
        name = model.name,
        airDate = model.airDate.toString(),
        episode = model.episode,
        characters = model.characters,
        url = model.url,
        created = model.created.toString()
    )
    fun entityToResponse(entity: EpisodeEntity): EpisodeResponse = EpisodeResponse(
        id = entity.id,
        name = entity.name,
        airDate = entity.airDate,
        episode = entity.episode,
        url = entity.url,
        characters = entity.characters,
        created = entity.created
    )
    fun responseToEntity(response: EpisodeResponse): EpisodeEntity = EpisodeEntity(
        id = response.id,
        name = response.name,
        airDate = response.airDate,
        episode = response.episode,
        url = response.url,
        characters = response.characters,
        created = response.created
    )
}