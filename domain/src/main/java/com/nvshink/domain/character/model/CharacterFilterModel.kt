package com.nvshink.domain.character.model

/**
 * This data class is used to store information about which filtering options have been selected. If option is null, it means that it is not selected.
 * @param name The name of the character.
 * @param status The status of the character ('Alive', 'Dead' or 'unknown').
 * @param species The species of the character.
 * @param type The type or subspecies of the character.
 * @param gender The gender of the character ('Female', 'Male', 'Genderless' or 'unknown').
 */
data class CharacterFilterModel(
    val name: String?,
    val status: CharacterStatus?,
    val species: String?,
    val type: String?,
    val gender: CharacterGender?
)