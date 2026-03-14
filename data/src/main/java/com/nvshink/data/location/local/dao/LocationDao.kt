package com.nvshink.data.location.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Upsert
import com.nvshink.data.location.local.entity.LocationEntity
import com.nvshink.data.location.local.entity.LocationRemoteKey
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Upsert
    suspend fun upsertLocation(locationEntity: LocationEntity)

    @Upsert
    suspend fun upsertLocations(locationEntities: List<LocationEntity>)

    @Query(
        """
        SELECT * FROM locations 
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:type IS NULL OR type LIKE '%' || :type || '%')
        AND (:dimension IS NULL OR dimension LIKE '%' || :dimension || '%')
        ORDER BY id ASC
    """
    )
    fun getPagingSource(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ): PagingSource<Int, LocationEntity>

    @Query("SELECT COUNT(*) FROM location_remote_keys")
    fun getLocationsCountFlow(): Flow<Int>

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

    @Upsert
    suspend fun upsertRemoteKeys(remoteKeys: List<LocationRemoteKey>)

    @Query("SELECT * FROM location_remote_keys WHERE locationId = :locationId")
    suspend fun getRemoteKeyByLocationId(locationId: Int): LocationRemoteKey?

    @Query("DELETE FROM location_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM locations")
    suspend fun clearLocations()
}