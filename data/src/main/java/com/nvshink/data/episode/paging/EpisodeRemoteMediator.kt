package com.nvshink.data.episode.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nvshink.data.episode.local.dao.EpisodeDao
import com.nvshink.data.episode.local.entity.EpisodeEntity
import com.nvshink.data.episode.local.entity.EpisodeRemoteKey
import com.nvshink.data.episode.network.service.EpisodeService
import com.nvshink.data.episode.utils.toEntity
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.domain.episode.model.EpisodeFilterModel

@OptIn(ExperimentalPagingApi::class)
class EpisodeRemoteMediator(
    private val database: RickAndMortyWikiDB,
    private val dao: EpisodeDao,
    private val service: EpisodeService,
    private val filterModel: EpisodeFilterModel
) : RemoteMediator<Int, EpisodeEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
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
            val response = service.getGetListOfEpisodesByPageAndParams(
                page = page,
                name = filterModel.name ?: "",
                episode = filterModel.episode ?: "",
            )
            val episodes = response.results.map { it.toEntity() }
            val endOfPaginationReached = episodes.isEmpty() || response.info.next == null
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            val keys = episodes.map { episode ->
                EpisodeRemoteKey(
                    episodeId = episode.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dao.clearRemoteKeys()
                    dao.clearEpisodes()
                }
                dao.upsertRemoteKeys(keys)
                dao.upsertEpisodes(episodes)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, EpisodeEntity>
    ): EpisodeRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { episode ->
                dao.getRemoteKeyByEpisodeId(episode.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, EpisodeEntity>
    ): EpisodeRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { episode ->
                dao.getRemoteKeyByEpisodeId(episode.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, EpisodeEntity>
    ): EpisodeRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { episodeId ->
                dao.getRemoteKeyByEpisodeId(episodeId)
            }
        }
    }
}
