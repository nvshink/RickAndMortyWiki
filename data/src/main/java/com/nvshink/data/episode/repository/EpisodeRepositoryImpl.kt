package com.nvshink.data.episode.repository

import android.util.Log
import com.nvshink.data.episode.local.dao.EpisodeDao
import com.nvshink.data.episode.network.service.EpisodeService
import com.nvshink.data.episode.utils.EpisodeMapper
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.episode.utils.EpisodeSortFields
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import com.nvshink.domain.resource.SortTypes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EpisodeRepositoryImpl @Inject constructor(
    private val dao: EpisodeDao,
    private val service: EpisodeService
) : EpisodeRepository {
    override suspend fun getListOfEpisodes(
        sortType: SortTypes,
        sortField: EpisodeSortFields,
        pageInfoModel: PageInfoModel,
        filterModel: EpisodeFilterModel
    ): Flow<Resource<Pair<PageInfoModel?, List<EpisodeModel>>>> = flow {
        emit(Resource.Loading)
        try {
            //Make query
            val queryString =
                if (pageInfoModel.prev != null) {
                    pageInfoModel.next
                } else {
                    "name=${filterModel.name ?: ""}&episode=${filterModel.episode ?: ""}"
                }
            //If the function is called, but the query is empty, an empty value is returned.
            if (queryString != null) {
                val response = service.getGetListOfEpisodes(queryString)

                //Separate info and result
                val responseInfo = response.info
                val responseResult = response.results


                val newEpisodePageInfoModel = pageInfoModel.copy(
                    next = responseInfo.next,
                    prev = responseInfo.prev
                )

                //White result in local DB
                responseResult.forEach {
                    dao.upsertEpisode(EpisodeMapper.responseToEntity(it))
                }
                // Return episodes
                emit(
                    Resource.Success(
                        Pair(
                            first = newEpisodePageInfoModel,
                            second = responseResult.map {
                                EpisodeMapper.responseToModel(it)
                            }
                        )
                    )
                )
            } else emit(
                Resource.Success(
                    Pair(
                        first = pageInfoModel,
                        second = emptyList()
                    )
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", e.message ?: "")
            //Try load from cache
            try {
                dao.getEpisodes(
                    name = filterModel.name,
                    episode = filterModel.episode
                ).map {
                    it.map { episodeEntity ->
                        EpisodeMapper.entityToModel(episodeEntity)
                    }
                }.collect { newsModel ->
                    emit(
                        Resource.Success(
                            data = Pair(
                                first = null,
                                second = newsModel
                            ),
                            isLocal = true,
                            onlineException = e
                        )
                    )

                }
            } catch (dbException: Throwable) {
                emit(Resource.Error(dbException, Pair(first = null, second = emptyList())))
            }
        }
    }

    /**
     * Get list of episodes by list id.
     * @param ids List of Episode id numbers.
     */
    override suspend fun getEpisodesByIds(ids: List<Int>): Flow<Resource<List<EpisodeModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                var idsString = ""
                ids.forEach {
                    idsString += "${it},"
                }
                val response = service.getGetEpisodesByIds(idsString)
                response.forEach {
                    dao.upsertEpisode(EpisodeMapper.responseToEntity(it))
                }
                emit(Resource.Success(response.map {
                    EpisodeMapper.responseToModel(it)
                }))
            } catch (e: Exception) {
                Log.d("DATA_LOAD", e.message ?: "")
                try {
                    val cachedEpisodes: MutableList<EpisodeModel> = mutableListOf()
                    ids.map {
                        dao.getEpisodesById(it).collect { cachedEntity ->
                            val cachedModel = EpisodeMapper.entityToModel(cachedEntity)
                            cachedEpisodes.add(cachedModel.id, cachedModel)
                        }
                    }
                    emit(
                        Resource.Success(
                            data = cachedEpisodes,
                            isLocal = true,
                            onlineException = e
                        )
                    )
                } catch (dbException: Throwable) {
                    emit(Resource.Error(dbException, emptyList()))
                }
            }
        }
}