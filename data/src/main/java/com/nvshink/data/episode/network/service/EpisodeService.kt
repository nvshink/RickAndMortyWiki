package com.nvshink.data.episode.network.service

import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.data.generic.network.response.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EpisodeService {
    @GET("episode")
    suspend fun getGetListOfEpisodes(
        @Query("query") query: String
    ) : PageResponse<EpisodeResponse>
    @GET("episode/{ids}")
    suspend fun getGetEpisodesByIds(
        @Path("ids") id: String
    ) : List<EpisodeResponse>
}
