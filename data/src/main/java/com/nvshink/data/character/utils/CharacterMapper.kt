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

    fun CharacterEntity.toModel(): CharacterModel = CharacterModel(
        id = id,
        name = name,
        status = when (status) {
            "Alive" -> CharacterStatus.ALIVE
            "Dead" -> CharacterStatus.DEAD
            else -> CharacterStatus.UNKNOWN
        },
        species = species,
        type = type,
        gender = when (gender) {
            "Male" -> CharacterGender.MALE
            "Female" -> CharacterGender.FEMALE
            "Genderless" -> CharacterGender.GENDERLESS
            else -> CharacterGender.UNKNOWN
        },
        origin = CharacterLocationModel(
            id = origin.locationId,
            name = origin.name,
            url = origin.url
        ),
        location = CharacterLocationModel(
            id = location.locationId,
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = ZonedDateTime.parse(created)
    )

    fun CharacterModel.toEntity(): CharacterEntity = CharacterEntity(
        id = id,
        name = name,
        status = when (status) {
            CharacterStatus.ALIVE -> "Alive"
            CharacterStatus.DEAD -> "Dead"
            CharacterStatus.UNKNOWN -> "unknown"
        },
        species = species,
        type = type,
        gender = when (gender) {
            CharacterGender.MALE -> "Male"
            CharacterGender.FEMALE -> "Female"
            CharacterGender.GENDERLESS -> "Genderless"
            CharacterGender.UNKNOWN -> "unknown"
        },
        origin = CharacterLocationEntity(
            locationId = origin.id,
            name = origin.name,
            url = origin.url
        ),
        location = CharacterLocationEntity(
            locationId = origin.id,
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created.toString()
    )

    fun CharacterResponse.toModel(): CharacterModel = CharacterModel(
        id = id,
        name = name,
        status = when (status) {
            "Alive" -> CharacterStatus.ALIVE
            "Dead" -> CharacterStatus.DEAD
            else -> CharacterStatus.UNKNOWN
        },
        species = species,
        type = type,
        gender = when (gender) {
            "Male" -> CharacterGender.MALE
            "Female" -> CharacterGender.FEMALE
            "Genderless" -> CharacterGender.GENDERLESS
            else -> CharacterGender.UNKNOWN
        },
        origin = CharacterLocationModel(
            id = urlToId(origin.url),
            name = origin.name,
            url = origin.url
        ),
        location = CharacterLocationModel(
            id = urlToId(location.url),
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = ZonedDateTime.parse(created)
    )

    fun CharacterModel.toResponse(): CharacterResponse = CharacterResponse(
        id = id,
        name = name,
        status = when (status) {
            CharacterStatus.ALIVE -> "Alive"
            CharacterStatus.DEAD -> "Dead"
            CharacterStatus.UNKNOWN -> "unknown"
        },
        species = species,
        type = type,
        gender = when (gender) {
            CharacterGender.MALE -> "Male"
            CharacterGender.FEMALE -> "Female"
            CharacterGender.GENDERLESS -> "Genderless"
            CharacterGender.UNKNOWN -> "unknown"
        },
        origin = CharacterLocationResponse(name = origin.name, url = origin.url),
        location = CharacterLocationResponse(name = location.name, url = location.url),
        image = image,
        episode = episode,
        url = url,
        created = created.toString()
    )

    fun CharacterEntity.toResponse(): CharacterResponse = CharacterResponse(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = CharacterLocationResponse(
            name = origin.name,
            url = origin.url
        ),
        location = CharacterLocationResponse(
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created
    )

    fun CharacterResponse.toEntity(): CharacterEntity = CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = CharacterLocationEntity(
            locationId = urlToId(origin.url),
            name = origin.name,
            url = origin.url
        ),
        location = CharacterLocationEntity(
            locationId = urlToId(origin.url),
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created
    )

    private fun urlToId(url: String): Int? {
        return if(url.isNotEmpty()) url.substringAfterLast("/").toInt() else null
    }