package com.nvshink.data.episode.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.RoomRawQuery
import com.nvshink.data.episode.local.dao.EpisodeDao
import com.nvshink.data.episode.network.response.EpisodeResponse
import com.nvshink.data.episode.network.service.EpisodeService
import com.nvshink.data.episode.paging.EpisodeRemoteMediator
import com.nvshink.data.episode.utils.toEntity
import com.nvshink.data.episode.utils.toModel
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.data.generic.network.exception.ResourceNotFoundException
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map
import kotlin.coroutines.cancellation.CancellationException

class EpisodeRepositoryImpl @Inject constructor(
    private val dao: EpisodeDao,
    private val service: EpisodeService,
    private val database: RickAndMortyWikiDB
) : EpisodeRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getEpisodesStream(
        filterModel: EpisodeFilterModel
    ): Flow<PagingData<EpisodeModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = true
            ),
            remoteMediator = EpisodeRemoteMediator(
                database = database,
                dao = dao,
                service = service,
                filterModel = filterModel
            ),
            pagingSourceFactory = {
                dao.getPagingSource()
            }
        ).flow.map { pagingData -> pagingData.map { entity -> entity.toModel() } }
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
                    dao.upsertEpisode(it.toEntity())
                }
                emit(Resource.Success(response.map {
                    it.toModel()
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
            dao.upsertEpisode(response.toEntity())
            emit(
                Resource.Success(
                    (response.toModel())
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
                    episodeEntity.toModel()
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
                    episodes.map { it.toModel() }
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
                        data = cachedEntity.toModel()
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