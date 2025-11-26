package com.nvshink.data.episode.network.service

import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.data.generic.network.response.PageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class EpisodeService @Inject constructor(
    private val client: HttpClient
){
    private val basePath = "episode/"
    suspend fun getGetListOfEpisodesByParams(
        name: String,
        episode: String
    ): PageResponse<EpisodeResponse> {
        return client.get(urlString = basePath) {
            parameter("name", name)
            parameter("episode", episode)
        }.body<PageResponse<EpisodeResponse>>()
    }
    suspend fun getGetListOfEpisodesByUrl(url: String): PageResponse<EpisodeResponse> {
        return client.get(urlString = url).body<PageResponse<EpisodeResponse>>()
    }
    suspend fun getGetListOfEpisodesByPath(path: String): List<EpisodeResponse> {
        return client.get(urlString = "$basePath$path").body<List<EpisodeResponse>>()
    }
    suspend fun getGetEpisodeById(id: Int): EpisodeResponse {
        return client.get(urlString = "$basePath$id").body<EpisodeResponse>()
    }
}
