package com.nvshink.data.location.repository

import android.util.Log
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

class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
    private val service: LocationService
) : LocationRepository {
    /**
     * Get list of locations and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    override suspend fun getLocations(
        pageInfoModel: PageInfoModel?,
        filterModel: LocationFilterModel
    ): Flow<Resource<Pair<PageInfoModel?, List<LocationModel>>>> = flow {
        emit(Resource.Loading)
        try {
            //If the function is called, but the query is empty, an empty value is returned.
            var response: PageResponse<LocationResponse>
            if (pageInfoModel != null) {
                if (pageInfoModel.next != null) {
                    response = service.getGetListOfLocationsByUrl(pageInfoModel.next!!)
                } else {
                    emit(
                        Resource.Success(
                            Pair(
                                first = pageInfoModel,
                                second = emptyList()
                            )
                        )
                    )
                    return@flow
                }
            } else {
                response = service.getGetListOfLocationsByParams(
                    name = filterModel.name ?: "",
                    type = filterModel.type ?: "",
                    dimension = filterModel.dimension ?: ""
                )
            }

            //Separate info and result
            val responseInfo = response.info
            val responseResult = response.results


            val newPageInfoModel = pageInfoModel?.copy(
                next = responseInfo.next,
                prev = responseInfo.prev
            )

            //White result in local DB
            responseResult.forEach {
                dao.upsertLocation(LocationMapper.responseToEntity(it))
            }

            // Return locations
            emit(
                Resource.Success(
                    Pair(
                        first = newPageInfoModel,
                        second = responseResult.map {
                            LocationMapper.responseToModel(it)
                        }
                    )
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Location error: ${e.message}")
            //Try load from cache
            try {
                dao.getLocations(
                    name = filterModel.name,
                    type = filterModel.type,
                    dimension = filterModel.dimension
                ).map {
                    it.map { locationEntity ->
                        LocationMapper.entityToModel(locationEntity)
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
                Log.d("DATA_LOAD", "Location db error: ${e.message}")
                emit(Resource.Error(dbException, Pair(first = null, second = emptyList())))
            }
        }
    }

    /**
     * Get list of locations by list id.
     * @param ids List of Location id numbers.
     */
    override suspend fun getLocationsByIds(ids: List<Int>): Flow<Resource<List<LocationModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                var path = ""
                ids.forEach { id -> path += "$id," }
                val response = service.getGetLocationsByPath(path.dropLast(1))
                response.forEach {
                    dao.upsertLocation(LocationMapper.responseToEntity(it))
                }
                emit(Resource.Success(response.map {
                    LocationMapper.responseToModel(it)
                }))
            } catch (e: Exception) {
                Log.d("DATA_LOAD", "Location by ids error: ${e.message}")
                try {
                    val cachedLocations: MutableList<LocationModel> = mutableListOf()
                    ids.map {
                        dao.getLocationById(it).collect { cachedEntity ->
                            val cachedModel = LocationMapper.entityToModel(cachedEntity)
                            cachedLocations.add(cachedModel.id, cachedModel)
                        }
                    }
                    emit(
                        Resource.Success(
                            data = cachedLocations,
                            isLocal = true,
                            onlineException = e
                        )
                    )
                } catch (dbException: Throwable) {
                    emit(Resource.Error(dbException, emptyList()))
                }
            }
        }

    override suspend fun getLocationById(id: Int): Flow<Resource<LocationModel>> = flow {
        emit(Resource.Loading)
        try {
            Log.d("DATA_LOAD", "Location id response: ${id}")
            val response = service.getGetLocationById(id)
            Log.d("DATA_LOAD", "Location by id response: ${response}")
            dao.upsertLocation(LocationMapper.responseToEntity(response))
            emit(
                Resource.Success(
                    (LocationMapper.responseToModel(response))
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Location by id error: ${e.message}")
            try {
                dao.getLocationById(id).collect { cachedEntity ->
                    emit(
                        Resource.Success(
                            data = LocationMapper.entityToModel(cachedEntity),
                            isLocal = true,
                            onlineException = e
                        )
                    )
                }

            } catch (dbException: Throwable) {
                emit(Resource.Error(dbException, null))
            }
        }
    }
}