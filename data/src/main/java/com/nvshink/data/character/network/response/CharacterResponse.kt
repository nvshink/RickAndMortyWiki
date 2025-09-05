package com.nvshink.data.character.network.response

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender:	String,
    val origin:	CharacterLocationResponse,
    val location: CharacterLocationResponse,
    val image:	String,
    val episode: List<String>,
    val url: String,
    val created: String
)
