package com.nvshink.data.character.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.data.character.local.entity.CharacterRemoteKey
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.character.utils.toEntity
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.domain.character.model.CharacterFilterModel

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val database: RickAndMortyWikiDB,
    private val dao: CharacterDao,
    private val service: CharacterService,
    private val filterModel: CharacterFilterModel
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
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
            val response = service.getGetListOfCharactersByPageAndParams(
                page = page,
                name = filterModel.name ?: "",
                status = filterModel.status?.name ?: "",
                species = filterModel.species ?: "",
                type = filterModel.type ?: "",
                gender = filterModel.gender?.name ?: "",
            )
            val characters = response.results.map { it.toEntity() }
            val endOfPaginationReached = characters.isEmpty() || response.info.next == null
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            val keys = characters.map { character ->
                CharacterRemoteKey(
                    characterId = character.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            database.withTransaction {
                dao.upsertRemoteKeys(keys)
                dao.upsertCharacters(characters)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CharacterEntity>
    ): CharacterRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                dao.getRemoteKeyByCharacterId(character.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CharacterEntity>
    ): CharacterRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { character ->
                dao.getRemoteKeyByCharacterId(character.id )
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CharacterEntity>
    ): CharacterRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { characterId ->
                dao.getRemoteKeyByCharacterId(characterId)
            }
        }
    }
}
