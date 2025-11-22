package com.nvshink.domain.character.model

import com.nvshink.domain.location.model.LocationModel
import java.time.ZonedDateTime

/**
 * @param id The id of the character.
 * @param name The name of the character.
 * @param status The status of the character ('Alive', 'Dead' or 'unknown').
 * @param species The species of the character.
 * @param type The type or subspecies of the character.
 * @param gender The gender of the character ('Female', 'Male', 'Genderless' or 'unknown').
 * @param origin Name and link to the character's origin location.
 * @param location Name and link to the character's last known location endpoint.
 * @param image Link to the character's image. All images are 300x300px and most are medium shots or portraits since they are intended to be used as avatars.
 * @param episode List of episodes in which this character appeared.
 * @param url Link to the character's own URL endpoint.
 * @param created Time at which the character was created in the database.
 */

data class CharacterModel(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender:	CharacterGender,
    val origin:	CharacterLocationModel,
    val location: CharacterLocationModel,
    val image:	String,
    val episode: List<String>,
    val url: String,
    val created: ZonedDateTime
)
