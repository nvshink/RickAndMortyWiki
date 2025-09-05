package com.nvshink.data.generic.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.data.episode.local.dao.EpisodeDao
import com.nvshink.data.episode.local.entity.EpisodeEntity
import com.nvshink.data.generic.local.room.utils.Converters
import com.nvshink.data.location.local.dao.LocationDao
import com.nvshink.data.location.local.entity.LocationEntity

@Database(
    entities = [CharacterEntity::class, LocationEntity::class, EpisodeEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RickAndMortyWikiDB : RoomDatabase() {
    abstract val characterDao: CharacterDao
    abstract val locationDao: LocationDao
    abstract val episodeDao: EpisodeDao
}