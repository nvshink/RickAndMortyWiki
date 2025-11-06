package com.nvshink.data.character.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.nvshink.data.character.local.converter.CharacterTypeConverter
import com.nvshink.data.character.local.entity.CharacterLocationEntity
import kotlinx.serialization.SerialName

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender:	String,
    @SerialName("origin")
    @TypeConverters(CharacterTypeConverter::class)
    val origin: CharacterLocationEntity,
    @SerialName("location")
    @TypeConverters(CharacterTypeConverter::class)
    val location: CharacterLocationEntity,
    val image:	String,
    val episode: List<String>,
    val url: String,
    val created: String
)
