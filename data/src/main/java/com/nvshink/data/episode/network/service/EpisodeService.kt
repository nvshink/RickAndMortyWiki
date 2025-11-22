package com.nvshink.data.episode.network.service

import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.data.generic.network.response.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface EpisodeService {
    @GET("episode")
    suspend fun getGetListOfEpisodesByParams(
        @Query("name") name: String,
        @Query("episode") episode: String,
    ) : PageResponse<EpisodeResponse>
    @GET
    suspend fun getGetListOfEpisodesByUrl(
        @Url url: String
    ) : PageResponse<EpisodeResponse>
    @GET("episode/{path}")
    suspend fun getGetEpisodesByPath(
        @Path("path") path: String
    ) : List<EpisodeResponse>
    @GET("episode/{id}")
    suspend fun getGetEpisodeById(
        @Path("id") id: Int
    ) : EpisodeResponse
}
