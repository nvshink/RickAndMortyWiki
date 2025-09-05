package com.nvshink.data.location.network.response

import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class LocationResponse(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String
)
