package com.nvshink.data.character.network.service

import com.nvshink.data.character.network.response.CharacterResponse
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.data.location.network.response.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CharacterService {
    @GET("character")
    suspend fun getGetListOfCharactersByParams(
        @Query("name") name: String,
        @Query("status") status: String,
        @Query("species") species: String,
        @Query("type") type: String,
        @Query("gender") gender: String,
    ) : PageResponse<CharacterResponse>
    @GET
    suspend fun getGetListOfCharactersByUrl(
        @Url url: String
    ) : PageResponse<CharacterResponse>
    @GET("character/{path}")
    suspend fun getGetCharactersByPath(
        @Path("path") path: String
    ) : List<CharacterResponse>
    @GET("character/{id}")
    suspend fun getGetCharacterById(
        @Path("id") id: Int
    ) : CharacterResponse
}