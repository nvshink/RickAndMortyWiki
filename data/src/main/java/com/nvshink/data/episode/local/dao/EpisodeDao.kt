package com.nvshink.data.episode.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Upsert
import com.nvshink.data.episode.local.entity.EpisodeEntity
import com.nvshink.data.episode.local.entity.EpisodeRemoteKey
import com.nvshink.data.location.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Upsert
    suspend fun upsertEpisode(episodeEntity: EpisodeEntity)

    @Upsert
    suspend fun upsertEpisodes(episodeEntities: List<EpisodeEntity>)

    @Query("SELECT * FROM episodes ORDER BY id ASC")
    fun getPagingSource(): PagingSource<Int, EpisodeEntity>

    @Query("SELECT COUNT(*) FROM episode_remote_keys")
    fun getEpisodesCountFlow(): Flow<Int>

    /**
     * Return list of episodes to the entered conditions
     */
    @RawQuery(observedEntities = [EpisodeEntity::class])
    fun getEpisodes(
        query: RoomRawQuery
    ): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun getEpisodesById(
        id: Int
    ): Flow<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id IN (:ids)")
    fun getEpisodesByIds(
        ids: List<Int>
    ): Flow<List<EpisodeEntity>>

    @Upsert
    suspend fun upsertRemoteKeys(remoteKeys: List<EpisodeRemoteKey>)

    @Query("SELECT * FROM episode_remote_keys WHERE episodeId = :episodeId")
    suspend fun getRemoteKeyByEpisodeId(episodeId: Int): EpisodeRemoteKey?

    @Query("DELETE FROM episode_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM episodes")
    suspend fun clearEpisodes()
}