package com.nvshink.data.location.utils


import com.nvshink.data.location.local.entity.LocationEntity
import com.nvshink.data.location.network.response.LocationResponse
import com.nvshink.domain.location.model.LocationModel
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

object LocationMapper {
    fun entityToModel(entity: LocationEntity): LocationModel = LocationModel(
        id = entity.id,
        name = entity.name,
        type = entity.type,
        dimension = entity.dimension,
        residents = Json.decodeFromString(entity.residents),
        url = entity.url,
        created = ZonedDateTime.parse(entity.created)
    )
    fun modelToEntity(model: LocationModel): LocationEntity = LocationEntity(
        id = model.id,
        name = model.name,
        type = model.type,
        dimension = model.dimension,
        residents = Json.encodeToString(model.residents),
        url = model.url,
        created = model.created.toString()
    )

    fun responseToModel(response: LocationResponse): LocationModel = LocationModel(
        id = response.id,
        name = response.name,
        type = response.type,
        dimension = response.dimension,
        residents = response.residents,
//        residents = Json.decodeFromString(response.residents),
        url = response.url,
        created = ZonedDateTime.parse(response.created)
    )
    fun modelToResponse(model: LocationModel): LocationResponse = LocationResponse(
        id = model.id,
        name = model.name,
        type = model.type,
        dimension = model.dimension,
        residents = model.residents,
//        residents = Json.encodeToString(model.residents),
        url = model.url,
        created = model.created.toString()
    )
    fun entityToResponse(entity: LocationEntity): LocationResponse = LocationResponse(
        id = entity.id,
        name = entity.name,
        type = entity.type,
        dimension = entity.dimension,
        residents = Json.decodeFromString(entity.residents),
        url = entity.url,
        created = entity.created
    )
    fun responseToEntity(response: LocationResponse): LocationEntity = LocationEntity(
        id = response.id,
        name = response.name,
        type = response.type,
        dimension = response.dimension,
        residents = Json.encodeToString(response.residents),
        url = response.url,
        created = response.created
    )
}