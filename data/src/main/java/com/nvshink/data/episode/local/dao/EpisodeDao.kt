package com.nvshink.data.episode.local.dao

import androidx.room.Dao
import androidx.room.Query
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
     * @param name The value for filtering by the `name` field. If `null`, it is not filtered.
     * @param episode The value for filtering by the `episode` field. If `null`, it is not filtered.
     */
    @Query(
        "SELECT * FROM episodes WHERE (:name IS NULL OR name = :name) " +
                "AND (:episode IS NULL OR episode = :episode)"
    )
    fun getEpisodes(
        name: String? = null,
        episode: String? = null,
    ): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun getEpisodesById(
        id: Int
    ): Flow<EpisodeEntity>
}