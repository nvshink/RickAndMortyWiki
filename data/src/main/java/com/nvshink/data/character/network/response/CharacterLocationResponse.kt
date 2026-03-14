package com.nvshink.data.character.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterLocationResponse(
    @SerialName(value = "name") val name: String,
    @SerialName(value = "url") val url: String,
)
