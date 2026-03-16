package com.nvshink.data.location.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.data.generic.network.exception.ResourceNotFoundException
import com.nvshink.data.location.local.dao.LocationDao
import com.nvshink.data.location.network.service.LocationService
import com.nvshink.data.location.paging.LocationRemoteMediator
import com.nvshink.data.location.utils.toEntity
import com.nvshink.data.location.utils.toModel
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map
import kotlin.coroutines.cancellation.CancellationException

class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
    private val service: LocationService,
    private val database: RickAndMortyWikiDB
) : LocationRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getLocationsStream(
        filterModel: LocationFilterModel
    ): Flow<PagingData<LocationModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = true
            ),
            remoteMediator = LocationRemoteMediator(
                database = database,
                dao = dao,
                service = service,
                filterModel = filterModel
            ),
            pagingSourceFactory = {
                dao.getPagingSource(
                    name = filterModel.name,
                    type = filterModel.type,
                    dimension = filterModel.dimension
                )
            }
        ).flow.map { pagingData -> pagingData.map { entity -> entity.toModel() } }
    }


    /**
     * Get list of locations by list id.
     * @param ids List of Location id numbers.
     */
    override suspend fun getLocationsByIdsApi(ids: List<Int>): Flow<Resource<List<LocationModel>>> =
        flow {
            emit(Resource.Loading())
            try {
                if (ids.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                    return@flow
                }
                var path = ""
                ids.filterNotNull().forEach { id -> path += "$id," }
                val response = service.getGetListOfLocationsByPath(path)
                response.forEach {
                    dao.upsertLocation(it.toEntity())
                }
                emit(Resource.Success(response.map {
                    it.toModel()
                }))
            } catch (ce: CancellationException) {
                throw ce
            } catch (resourceNotFound: ResourceNotFoundException) {
                Log.e("DATA_LOAD", "Locations error: ${resourceNotFound.message}")
                emit(
                    Resource.Success(
                        emptyList()
                    )
                )
            } catch (e: Exception) {
                emit(Resource.Error(exception = e))
            }
        }

    override suspend fun getLocationByIdApi(id: Int): Flow<Resource<LocationModel>> = flow {
        emit(Resource.Loading())
        try {
            val response = service.getGetLocationById(id)
            dao.upsertLocation(response.toEntity())
            emit(
                Resource.Success(
                    response.toModel()
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.e("DATA_LOAD", "Location by id error: ${resourceNotFound.message}")
            emit(
                Resource.Error(
                    exception = resourceNotFound
                )
            )
        } catch (e: Exception) {
            Log.e("DATA_LOAD", "Location by id error: ${e.message}")
            emit(Resource.Error(exception = e))
        }
    }


    override suspend fun getLocationsDB(
        filterModel: LocationFilterModel
    ): Flow<Resource<List<LocationModel>>> = flow {
        emit(Resource.Loading())
        //Try load from cache
        try {
            dao.getLocations(
                query = sqlLocationQueryBuilder(
                    name = filterModel.name,
                    type = filterModel.type,
                    dimension = filterModel.dimension
                )
            ).map {
                it.map { locationEntity ->
                    locationEntity.toModel()
                }
            }.collect { locations ->
                emit(
                    Resource.Success(
                        data = locations
                    )
                )
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.e("DATA_LOAD", "Location db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    override suspend fun getLocationsByIdsDB(ids: List<Int>): Flow<Resource<List<LocationModel>>> =
        flow {
            emit(Resource.Loading())
            try {
                dao.getLocationsByIds(ids).map { locations ->
                    locations.map { it.toModel() }
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
                Log.e("DATA_LOAD", "Location by ids db error: ${dbException.message}")
                emit(Resource.Error(exception = dbException))
            }
        }

    override suspend fun getLocationByIdDB(id: Int): Flow<Resource<LocationModel>> = flow {
        try {
            dao.getLocationById(id).collect { cachedEntity ->
                emit(
                    Resource.Success(
                        data = cachedEntity.toModel()
                    )
                )
            }

        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.e("DATA_LOAD", "Location by id db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    private fun sqlLocationQueryBuilder(
        name: String?,
        type: String?,
        dimension: String?
    ): RoomRawQuery {
        val selectionArgs = mutableListOf<String>()

        if (name != null) {
            selectionArgs.add("name LIKE '%$name%'")
        }

        if (type != null) {
            selectionArgs.add("type LIKE '$type'")
        }

        if (dimension != null) {
            selectionArgs.add("dimension LIKE '$dimension'")
        }

        val query =
            "SELECT * FROM locations ${if (selectionArgs.isNotEmpty()) "WHERE" else ""} ${
                selectionArgs.joinToString(
                    " AND "
                )
            }"
        return RoomRawQuery(query)
    }
}