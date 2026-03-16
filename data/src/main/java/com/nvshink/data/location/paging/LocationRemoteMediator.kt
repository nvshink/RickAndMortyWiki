package com.nvshink.data.location.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.data.location.local.dao.LocationDao
import com.nvshink.data.location.local.entity.LocationEntity
import com.nvshink.data.location.local.entity.LocationRemoteKey
import com.nvshink.data.location.network.service.LocationService
import com.nvshink.data.location.utils.toEntity
import com.nvshink.domain.location.model.LocationFilterModel

@OptIn(ExperimentalPagingApi::class)
class LocationRemoteMediator(
    private val database: RickAndMortyWikiDB,
    private val dao: LocationDao,
    private val service: LocationService,
    private val filterModel: LocationFilterModel
) : RemoteMediator<Int, LocationEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys?.nextKey == null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                nextKey
            }
        }

        return try {
            val response = service.getGetListOfLocationsByPageAndParams(
                page = page,
                name = filterModel.name ?: "",
                type = filterModel.type ?: "",
                dimension = filterModel.dimension ?: "",
            )
            val locations = response.results.map { it.toEntity() }
            val endOfPaginationReached = locations.isEmpty() || response.info.next == null
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            val keys = locations.map { location ->
                LocationRemoteKey(
                    locationId = location.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dao.clearRemoteKeys()
                    dao.clearLocations()
                }
                dao.upsertRemoteKeys(keys)
                dao.upsertLocations(locations)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, LocationEntity>
    ): LocationRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { location ->
                dao.getRemoteKeyByLocationId(location.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, LocationEntity>
    ): LocationRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { location ->
                dao.getRemoteKeyByLocationId(location.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, LocationEntity>
    ): LocationRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { locationId ->
                dao.getRemoteKeyByLocationId(locationId)
            }
        }
    }
}
