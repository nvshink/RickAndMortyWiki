package com.nvshink.domain.episode.repository

import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.episode.utils.EpisodeSortFields
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import com.nvshink.domain.resource.SortTypes
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    /**
     * Get list of episodes and page if data is remote:
     * @param sortType Indicates the order in which the list will be sorted.
     * @param sortField Determines which field will be sorted by.
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getListOfEpisodes(
        sortType: SortTypes,
        sortField: EpisodeSortFields,
        pageInfoModel: PageInfoModel,
        filterModel: EpisodeFilterModel
    ): Flow<Resource<Pair<PageInfoModel?, List<EpisodeModel>>>>


    /**
     * Get list of episodes by list id.
     * @param ids List of Episode id numbers.
     */
    suspend fun getEpisodesByIds(ids: List<Int>): Flow<Resource<List<EpisodeModel>>>
}