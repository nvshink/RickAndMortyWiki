package com.nvshink.data.generic.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageInfoResponse(
    @SerialName(value = "count") val count: Int,
    @SerialName(value = "pages") val pages: Int,
    @SerialName(value = "next") val next: String?,
    @SerialName(value = "prev") val prev: String?,
)
