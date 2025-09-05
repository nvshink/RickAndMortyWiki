package com.nvshink.data.location.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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
     * @param sortBy Specifies which field the list will be sorted by
     * @param name The value for filtering by the `name` field. If `null`, it is not filtered.
     * @param type The value for filtering by the `type` field. If `null`, it is not filtered.
     * @param dimension The value for filtering by the `dimension` field. If `null`, it is not filtered.
     */
    @Query(
        "SELECT * FROM locations WHERE (:name IS NULL OR name = :name) " +
                "AND (:type IS NULL OR type = :type) " +
                "AND (:dimension IS NULL OR dimension = :dimension) "
    )
    fun getLocations(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun getLocationById(
        id: Int
    ): Flow<LocationEntity>
}