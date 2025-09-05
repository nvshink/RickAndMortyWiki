package com.nvshink.data.location.network.service

import com.nvshink.data.generic.network.response.PageInfoResponse
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.data.location.network.response.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface LocationService{
    @GET("location")
    suspend fun getGetListOfLocationsByParams(
        @Query("name") name: String,
        @Query("type") type: String,
        @Query("dimension") dimension: String,
    ) : PageResponse<LocationResponse>
    @GET
    suspend fun getGetListOfLocationsByUrl(
        @Url url: String
    ) : PageResponse<LocationResponse>
    @GET("location/{path}")
    suspend fun getGetLocationsByPath(
        @Path("path") path: String
    ) : List<LocationResponse>
    @GET("location/{id}")
    suspend fun getGetLocationById(
        @Path("id") id: Int
    ) : LocationResponse
}
