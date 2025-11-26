package com.nvshink.data.generic.network.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)
