package com.nvshink.data.location.repository

import android.util.Log
import androidx.room.RoomRawQuery
import com.nvshink.data.character.utils.CharacterMapper
import com.nvshink.data.generic.network.exception.ResourceNotFoundException
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.data.location.local.dao.LocationDao
import com.nvshink.data.location.network.response.LocationResponse
import com.nvshink.data.location.network.service.LocationService
import com.nvshink.data.location.utils.LocationMapper
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map
import kotlin.coroutines.cancellation.CancellationException

class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
    private val service: LocationService
) : LocationRepository {
    /**
     * Get list of locations and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    override suspend fun getLocationsApi(
        pageInfoModel: PageInfoModel,
        filterModel: LocationFilterModel
    ): Flow<Pair<PageInfoModel, Resource<List<LocationModel>>>> = flow {
        emit(Pair(pageInfoModel, Resource.Loading))
        try {
            //If the function is called, but the query is empty, an empty value is returned.
            var response: PageResponse<LocationResponse>
            if (pageInfoModel.next != null) {
                response = service.getGetListOfLocationsByUrl(pageInfoModel.next!!)
            } else if (pageInfoModel.prev == null) {
                response = service.getGetListOfLocationsByParams(
                    name = filterModel.name ?: "",
                    type = filterModel.type ?: "",
                    dimension = filterModel.dimension ?: ""
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

            val newPageInfoModel = pageInfoModel.copy(
                next = responseInfo.next,
                prev = responseInfo.prev
            )

            //White result in local DB
            responseResult.forEach {
                dao.upsertLocation(LocationMapper.responseToEntity(it))
            }

            // Return locations
            emit(
                Pair(
                    first = newPageInfoModel,
                    second = Resource.Success(responseResult.map {
                        LocationMapper.responseToModel(it)
                    }
                    )
                )
            )
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.d("DATA_LOAD", "Locations error: ${resourceNotFound.message}")
            emit(
                Pair(
                    first = pageInfoModel,
                    second = Resource.Success(
                        emptyList()
                    )
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Location db error: ${e.message}")
            emit(Pair(pageInfoModel,Resource.Error(e)))
        }
    }

    /**
     * Get list of locations by list id.
     * @param ids List of Location id numbers.
     */
    override suspend fun getLocationsByIdsApi(ids: List<Int>): Flow<Resource<List<LocationModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                var path = ""
                ids.forEach { id -> path += "$id," }
                val response = service.getGetListOfLocationsByPath(path)
                response.forEach {
                    dao.upsertLocation(LocationMapper.responseToEntity(it))
                }
                emit(Resource.Success(response.map {
                    LocationMapper.responseToModel(it)
                }))
            } catch (ce: CancellationException) {
                throw ce
            } catch (resourceNotFound: ResourceNotFoundException) {
                Log.d("DATA_LOAD", "Locations error: ${resourceNotFound.message}")
                emit(
                    Resource.Success(
                        emptyList()
                    )
                )
            } catch (e: Exception) {
                emit(Resource.Error(e))
            }
        }

    override suspend fun getLocationByIdApi(id: Int): Flow<Resource<LocationModel>> = flow {
        emit(Resource.Loading)
        try {
            val response = service.getGetLocationById(id)
            dao.upsertLocation(LocationMapper.responseToEntity(response))
            emit(
                Resource.Success(
                    (LocationMapper.responseToModel(response))
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.d("DATA_LOAD", "Location by id error: ${resourceNotFound.message}")
            emit(
                Resource.Error(
                    exception = resourceNotFound
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Location by id error: ${e.message}")
            emit(Resource.Error(e))
        }
    }


    override suspend fun getLocationsDB(
        filterModel: LocationFilterModel
    ): Flow<Resource<List<LocationModel>>> = flow {
        emit(Resource.Loading)
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
                    LocationMapper.entityToModel(locationEntity)
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
            Log.d("DATA_LOAD", "Location db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    override suspend fun getLocationsByIdsDB(ids: List<Int>): Flow<Resource<List<LocationModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                dao.getLocationsByIds(ids).map { locations ->
                    locations.map { LocationMapper.entityToModel(entity = it) }
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
                Log.d("DATA_LOAD", "Location by ids db error: ${dbException.message}")
                emit(Resource.Error(exception = dbException))
            }
        }

    override suspend fun getLocationByIdDB(id: Int): Flow<Resource<LocationModel>> = flow {
        try {
            dao.getLocationById(id).collect { cachedEntity ->
                emit(
                    Resource.Success(
                        data = LocationMapper.entityToModel(cachedEntity)
                    )
                )
            }

        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.d("DATA_LOAD", "Location by id db error: ${dbException.message}")
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
            selectionArgs.add("status LIKE '$type'")
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