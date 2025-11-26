package com.nvshink.data.character.network.service

import com.nvshink.data.character.network.response.CharacterResponse
import com.nvshink.data.generic.network.response.PageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class CharacterService @Inject constructor(
    private val client: HttpClient
){
    private val basePath = "character/"
    suspend fun getGetListOfCharactersByParams(
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String
    ): PageResponse<CharacterResponse> {
        return client.get(urlString = basePath) {
                parameter("name", name)
                parameter("status", status)
                parameter("species", species)
                parameter("type", type)
                parameter("gender", gender)
            }.body<PageResponse<CharacterResponse>>()
    }
    suspend fun getGetListOfCharactersByUrl(url: String): PageResponse<CharacterResponse> {
        return client.get(urlString = url).body<PageResponse<CharacterResponse>>()
    }
    suspend fun getGetListOfCharactersByPath(path: String): List<CharacterResponse> {
        return client.get(urlString = "$basePath$path").body<List<CharacterResponse>>()
    }
    suspend fun getGetCharacterById(id: Int): CharacterResponse {
        return client.get(urlString = "$basePath$id").body<CharacterResponse>()
    }
}