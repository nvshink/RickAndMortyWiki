package com.nvshink.data.character.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nvshink.data.character.local.entity.CharacterLocationEntity

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender:	String,
    val origin: CharacterLocationEntity,
    val location: CharacterLocationEntity,
    val image:	String,
    val episode: List<String>,
    val url: String,
    val created: String
)
