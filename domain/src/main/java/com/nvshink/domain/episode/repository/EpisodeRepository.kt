package com.nvshink.domain.episode.repository

import androidx.paging.PagingData
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.resource.Resource
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getEpisodesStream(
        filterModel: EpisodeFilterModel
    ): Flow<PagingData<EpisodeModel>>

    /**
     * Get list of episodes by list id from API.
     * @param ids List of Episode id numbers.
     */
    suspend fun getEpisodesByIdsApi(ids: List<Int>): Flow<Resource<List<EpisodeModel>>>

    /**
     * Get episode by id from API.
     * @param id Episode id numbers.
     */
    suspend fun getEpisodeByIdApi(id: Int): Flow<Resource<EpisodeModel>>

    /**
     * Get list of episodes from DB:
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getEpisodesDB(
        filterModel: EpisodeFilterModel
    ): Flow<Resource<List<EpisodeModel>>>

    /**
     * Get list of episodes by list id from DB.
     * @param ids List of Episode id numbers.
     */
    suspend fun getEpisodesByIdsDB(ids: List<Int>): Flow<Resource<List<EpisodeModel>>>

    /**
     * Get episode by id from DB.
     * @param id Episode id numbers.
     */
    suspend fun getEpisodeByIdDB(id: Int): Flow<Resource<EpisodeModel>>
}