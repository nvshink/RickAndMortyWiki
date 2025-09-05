package com.nvshink.data.generic.local.room.utils

import androidx.room.TypeConverter
import com.nvshink.data.character.local.entity.CharacterLocationEntity
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromJsonToCharacterLocationEntity(string: String): CharacterLocationEntity {
        return Json.decodeFromString<CharacterLocationEntity>(string)
    }

    @TypeConverter
    fun fromCharacterLocationEntityToJson(locationEntity: CharacterLocationEntity): String {
        return Json.encodeToString(locationEntity)
    }

    @TypeConverter
    fun fromJsonToListStrings(string: String): List<String> {
        return Json.decodeFromString<List<String>>(string)
    }

    @TypeConverter
    fun fromListStringsToJson(list: List<String>): String {
        return Json.encodeToString(list)
    }
}
