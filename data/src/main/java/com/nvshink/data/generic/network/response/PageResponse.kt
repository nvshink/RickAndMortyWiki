package com.nvshink.data.generic.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    @SerialName("info")
    val info: PageInfoResponse,
    @SerialName("results")
    val results: List<T>
)
