package com.nvshink.data.character.utils

import android.util.Log
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.data.character.local.entity.CharacterLocationEntity
import com.nvshink.data.character.network.response.CharacterLocationResponse
import com.nvshink.data.character.network.response.CharacterResponse
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterLocationModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import java.time.ZonedDateTime

object CharacterMapper {
    fun entityToModel(entity: CharacterEntity): CharacterModel = CharacterModel(
        id = entity.id,
        name = entity.name,
        status = when (entity.status) {
            "Alive" -> CharacterStatus.ALIVE
            "Dead" -> CharacterStatus.DEAD
            else -> CharacterStatus.UNKNOWN
        },
        species = entity.species,
        type = entity.type,
        gender = when (entity.gender) {
            "Male" -> CharacterGender.MALE
            "Female" -> CharacterGender.FEMALE
            "Genderless" -> CharacterGender.GENDERLESS
            else -> CharacterGender.UNKNOWN
        },
        origin = CharacterLocationModel(
            id = entity.origin.locationId,
            name = entity.origin.name,
            url = entity.origin.url
        ),
        location = CharacterLocationModel(
            id = entity.location.locationId,
            name = entity.location.name,
            url = entity.location.url
        ),
        image = entity.image,
        episode = entity.episode,
        url = entity.url,
        created = ZonedDateTime.parse(entity.created)
    )

    fun modelToEntity(model: CharacterModel): CharacterEntity = CharacterEntity(
        id = model.id,
        name = model.name,
        status = when (model.status) {
            CharacterStatus.ALIVE -> "Alive"
            CharacterStatus.DEAD -> "Dead"
            CharacterStatus.UNKNOWN -> "unknown"
        },
        species = model.species,
        type = model.type,
        gender = when (model.gender) {
            CharacterGender.MALE -> "Male"
            CharacterGender.FEMALE -> "Female"
            CharacterGender.GENDERLESS -> "Genderless"
            CharacterGender.UNKNOWN -> "unknown"
        },
        origin = CharacterLocationEntity(
            locationId = model.origin.id,
            name = model.origin.name,
            url = model.origin.url
        ),
        location = CharacterLocationEntity(
            locationId = model.origin.id,
            name = model.location.name,
            url = model.location.url
        ),
        image = model.image,
        episode = model.episode,
        url = model.url,
        created = model.created.toString()
    )

    fun responseToModel(response: CharacterResponse): CharacterModel = CharacterModel(
        id = response.id,
        name = response.name,
        status = when (response.status) {
            "Alive" -> CharacterStatus.ALIVE
            "Dead" -> CharacterStatus.DEAD
            else -> CharacterStatus.UNKNOWN
        },
        species = response.species,
        type = response.type,
        gender = when (response.gender) {
            "Male" -> CharacterGender.MALE
            "Female" -> CharacterGender.FEMALE
            "Genderless" -> CharacterGender.GENDERLESS
            else -> CharacterGender.UNKNOWN
        },
        origin = CharacterLocationModel(
            id = urlToId(response.origin.url),
            name = response.origin.name,
            url = response.origin.url
        ),
        location = CharacterLocationModel(
            id = urlToId(response.location.url),
            name = response.location.name,
            url = response.location.url
        ),
        image = response.image,
        episode = response.episode,
        url = response.url,
        created = ZonedDateTime.parse(response.created)
    )

    fun modelToResponse(model: CharacterModel): CharacterResponse = CharacterResponse(
        id = model.id,
        name = model.name,
        status = when (model.status) {
            CharacterStatus.ALIVE -> "Alive"
            CharacterStatus.DEAD -> "Dead"
            CharacterStatus.UNKNOWN -> "unknown"
        },
        species = model.species,
        type = model.type,
        gender = when (model.gender) {
            CharacterGender.MALE -> "Male"
            CharacterGender.FEMALE -> "Female"
            CharacterGender.GENDERLESS -> "Genderless"
            CharacterGender.UNKNOWN -> "unknown"
        },
        origin = CharacterLocationResponse(name = model.origin.name, url = model.origin.url),
        location = CharacterLocationResponse(name = model.location.name, url = model.location.url),
        image = model.image,
        episode = model.episode,
        url = model.url,
        created = model.created.toString()
    )

    fun entityToResponse(entity: CharacterEntity): CharacterResponse = CharacterResponse(
        id = entity.id,
        name = entity.name,
        status = entity.status,
        species = entity.species,
        type = entity.type,
        gender = entity.gender,
        origin = CharacterLocationResponse(
            name = entity.origin.name,
            url = entity.origin.url
        ),
        location = CharacterLocationResponse(
            name = entity.location.name,
            url = entity.location.url
        ),
        image = entity.image,
        episode = entity.episode,
        url = entity.url,
        created = entity.created
    )

    fun responseToEntity(response: CharacterResponse): CharacterEntity = CharacterEntity(
        id = response.id,
        name = response.name,
        status = response.status,
        species = response.species,
        type = response.type,
        gender = response.gender,
        origin = CharacterLocationEntity(
            locationId = urlToId(response.origin.url),
            name = response.origin.name,
            url = response.origin.url
        ),
        location = CharacterLocationEntity(
            locationId = urlToId(response.origin.url),
            name = response.location.name,
            url = response.location.url
        ),
        image = response.image,
        episode = response.episode,
        url = response.url,
        created = response.created
    )

    private fun urlToId(url: String): Int? {
        return if(url.isNotEmpty()) url.substringAfterLast("/").toInt() else null
    }
}