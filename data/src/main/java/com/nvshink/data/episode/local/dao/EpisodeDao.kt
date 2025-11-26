package com.nvshink.data.episode.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Upsert
import com.nvshink.data.episode.local.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    /**
     * Save new and update existing episode
     */
    @Upsert
    suspend fun upsertEpisode(episodeEntity: EpisodeEntity)

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
}