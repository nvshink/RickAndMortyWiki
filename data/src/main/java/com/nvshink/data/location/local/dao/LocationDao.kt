package com.nvshink.data.location.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Upsert
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.data.episode.local.entity.EpisodeEntity
import com.nvshink.data.location.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    /**
     * Save new and update existing location
     */
    @Upsert
    suspend fun upsertLocation(locationEntity: LocationEntity)

    /**
     * Return list of locations to the entered conditions
     */
    @RawQuery(observedEntities = [LocationEntity::class])
    fun getLocations(
        query: RoomRawQuery
    ): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun getLocationById(
        id: Int
    ): Flow<LocationEntity>

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    fun getLocationsByIds(
        ids: List<Int>
    ): Flow<List<LocationEntity>>
}