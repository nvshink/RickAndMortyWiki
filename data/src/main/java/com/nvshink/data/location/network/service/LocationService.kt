package com.nvshink.data.location.network.service

import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.data.location.network.response.LocationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class LocationService @Inject constructor(
    private val client: HttpClient
){
    private val basePath = "location/"
    suspend fun getGetListOfLocationsByParams(
        name: String,
        type: String,
        dimension: String,
    ): PageResponse<LocationResponse> {
        return client.get(urlString = basePath) {
            parameter("name", name)
            parameter("type", type)
            parameter("dimension", dimension)
        }.body<PageResponse<LocationResponse>>()
    }
    suspend fun getGetListOfLocationsByUrl(url: String): PageResponse<LocationResponse> {
        return client.get(urlString = url).body<PageResponse<LocationResponse>>()
    }
    suspend fun getGetListOfLocationsByPath(path: String): List<LocationResponse> {
        return client.get(urlString = "$basePath$path").body<List<LocationResponse>>()
    }
    suspend fun getGetLocationById(id: Int): LocationResponse {
        return client.get(urlString = "$basePath$id").body<LocationResponse>()
    }
}
