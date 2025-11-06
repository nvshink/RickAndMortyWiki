package com.nvshink.data.character.local.converter

import androidx.room.TypeConverter
import com.nvshink.data.character.local.entity.CharacterLocationEntity
import kotlinx.serialization.json.Json

object CharacterTypeConverter {
        private val json = Json { ignoreUnknownKeys = true }

        @TypeConverter
        fun fromLocation(location: CharacterLocationEntity?): String? =
            if (location != null) json.encodeToString(location) else null

        @TypeConverter
        fun toLocation(jsonString: String?): CharacterLocationEntity? =
            if (!jsonString.isNullOrEmpty()) json.decodeFromString(jsonString) else null
}