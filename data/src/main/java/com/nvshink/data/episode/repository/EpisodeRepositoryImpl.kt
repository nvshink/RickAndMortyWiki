package com.nvshink.data.episode.repository

import android.util.Log
import androidx.room.RoomRawQuery
import com.nvshink.data.character.utils.CharacterMapper
import com.nvshink.data.episode.local.dao.EpisodeDao
import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.data.episode.network.service.EpisodeService
import com.nvshink.data.episode.utils.EpisodeMapper
import com.nvshink.data.generic.network.exception.ResourceNotFoundException
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map
import kotlin.coroutines.cancellation.CancellationException

class EpisodeRepositoryImpl @Inject constructor(
    private val dao: EpisodeDao,
    private val service: EpisodeService
) : EpisodeRepository {
    /**
     * Get list of episodes and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    override suspend fun getEpisodesApi(
        pageInfoModel: PageInfoModel,
        filterModel: EpisodeFilterModel
    ): Flow<Pair<PageInfoModel, Resource<List<EpisodeModel>>>> = flow {
        emit(Pair(pageInfoModel, Resource.Loading))
        try {
            //If the function is called, but the query is empty, an empty value is returned.
            var response: PageResponse<EpisodeResponse>
            if (pageInfoModel.next != null) {
                response = service.getGetListOfEpisodesByUrl(pageInfoModel.next!!)
            } else if (pageInfoModel.prev == null) {
                response = service.getGetListOfEpisodesByParams(
                    name = filterModel.name ?: "",
                    episode = filterModel.episode ?: ""
                )
            } else {
                emit(
                    Pair(
                        first = pageInfoModel,
                        second = Resource.Success(
                            emptyList()
                        )
                    )
                )
                return@flow
            }

            //Separate info and result
            val responseInfo = response.info
            val responseResult = response.results

            val newEpisodePageInfoModel = PageInfoModel(
                next = responseInfo.next,
                prev = responseInfo.prev
            )
            Log.d("TEST", responseResult.toString())


            //White result in local DB
            responseResult.forEach {
                Log.d("TEST", EpisodeMapper.responseToEntity(it).toString())

                dao.upsertEpisode(EpisodeMapper.responseToEntity(it))
            }

            // Return episodes
            emit(
                Pair(
                    first = newEpisodePageInfoModel,
                    second = Resource.Success(responseResult.map {
                        EpisodeMapper.responseToModel(it)
                    }
                    )
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.d("DATA_LOAD", "Episodes error: ${resourceNotFound.message}")
            emit(
                Pair(
                    first = pageInfoModel,
                    second = Resource.Success(
                        emptyList()
                    )
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Episodes error: ${e.message}")
            emit(Pair(pageInfoModel, Resource.Error(exception = e)))
        }
    }

    /**
     * Get list of episodes by list ids from API.
     * @param ids List of Episode id numbers.
     */
    override suspend fun getEpisodesByIdsApi(ids: List<Int>): Flow<Resource<List<EpisodeModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                var path = ""
                ids.forEach { id -> path += "$id," }
                val response = service.getGetListOfEpisodesByPath(path)
                response.forEach {
                    dao.upsertEpisode(EpisodeMapper.responseToEntity(it))
                }
                emit(Resource.Success(response.map {
                    EpisodeMapper.responseToModel(it)
                }))
            } catch (ce: CancellationException) {
                throw ce
            } catch (resourceNotFound: ResourceNotFoundException) {
                Log.d("DATA_LOAD", "Episodes error: ${resourceNotFound.message}")
                emit(
                    Resource.Success(
                        emptyList()
                    )
                )
            } catch (e: Exception) {
                Log.d("DATA_LOAD", "Episode by ids error: ${e.message}")
                emit(Resource.Error(exception = e))
            }
        }

    /**
     * Get episodes by id from API.
     * @param id Episode id number.
     */
    override suspend fun getEpisodeByIdApi(id: Int): Flow<Resource<EpisodeModel>> = flow {
        emit(Resource.Loading)
        try {
            val response = service.getGetEpisodeById(id)
            dao.upsertEpisode(EpisodeMapper.responseToEntity(response))
            emit(
                Resource.Success(
                    (EpisodeMapper.responseToModel(response))
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.d("DATA_LOAD", "Episodes error: ${resourceNotFound.message}")
            emit(
                Resource.Error(
                    exception = resourceNotFound
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Episode by id error: ${e.message}")
            emit(Resource.Error(exception = e))
        }
    }

    override suspend fun getEpisodesDB(
        filterModel: EpisodeFilterModel
    ): Flow<Resource<List<EpisodeModel>>> = flow {
        emit(Resource.Loading)
        //Try load from cache
        try {
            dao.getEpisodes(
                sqlEpisodeQueryBuilder(
                    name = filterModel.name,
                    episode = filterModel.episode
                )
            ).map {
                it.map { episodeEntity ->
                    EpisodeMapper.entityToModel(episodeEntity)
                }
            }.collect { episodes ->
                emit(
                    Resource.Success(
                        data = episodes
                    )
                )
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.d("DATA_LOAD", "Episodes db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    override suspend fun getEpisodesByIdsDB(ids: List<Int>): Flow<Resource<List<EpisodeModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                dao.getEpisodesByIds(ids).map { episodes ->
                    episodes.map { EpisodeMapper.entityToModel(entity = it) }
                }.collect {
                    emit(
                        Resource.Success(
                            data = it
                        )
                    )
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (dbException: Exception) {
                Log.d("DATA_LOAD", "Episodes by ids db error: ${dbException.message}")
                emit(Resource.Error(exception = dbException))
            }
        }

    override suspend fun getEpisodeByIdDB(id: Int): Flow<Resource<EpisodeModel>> = flow {
        try {
            dao.getEpisodesById(id).collect { cachedEntity ->
                emit(
                    Resource.Success(
                        data = EpisodeMapper.entityToModel(cachedEntity)
                    )
                )
            }

        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.d("DATA_LOAD", "Episodes by id db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    private fun sqlEpisodeQueryBuilder(
        name: String?,
        episode: String?
    ): RoomRawQuery {
        val selectionArgs = mutableListOf<String>()

        if (name != null) {
            selectionArgs.add("name LIKE '%$name%'")
        }

        if (episode != null) {
            selectionArgs.add("status LIKE '$episode'")
        }

        val query =
            "SELECT * FROM episodes ${if (selectionArgs.isNotEmpty()) "WHERE" else ""} ${
                selectionArgs.joinToString(
                    " AND "
                )
            }"
        return RoomRawQuery(query)
    }
}