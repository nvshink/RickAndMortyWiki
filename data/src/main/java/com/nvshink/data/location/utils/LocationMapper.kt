package com.nvshink.data.location.utils

import com.nvshink.data.location.local.entity.LocationEntity
import com.nvshink.data.location.network.response.LocationResponse
import com.nvshink.domain.location.model.LocationModel
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

fun LocationEntity.toModel(): LocationModel = LocationModel(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = Json.decodeFromString(residents),
    url = url,
    created = ZonedDateTime.parse(created)
)

fun LocationModel.toEntity(): LocationEntity = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = Json.encodeToString(residents),
    url = url,
    created = created.toString()
)

fun LocationResponse.toModel(): LocationModel = LocationModel(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = residents,
    url = url,
    created = ZonedDateTime.parse(created)
)

fun LocationModel.toResponse(): LocationResponse = LocationResponse(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = residents,
    url = url,
    created = created.toString()
)

fun LocationEntity.toResponse(): LocationResponse = LocationResponse(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = Json.decodeFromString(residents),
    url = url,
    created = created
)

fun LocationResponse.toEntity(): LocationEntity = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = Json.encodeToString(residents),
    url = url,
    created = created
)
