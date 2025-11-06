package com.nvshink.data.character.local.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterLocationEntity (
    @SerialName("location_id")
    val locationId: Int? = null,
    val name: String,
    val url: String
)