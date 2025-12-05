package com.nvshink.data.episode.network.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.character.utils.toEntity
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.data.generic.network.exception.HttpException
import com.nvshink.domain.character.model.CharacterModel
import kotlinx.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val db: RickAndMortyWikiDB,
    private val service: CharacterService
) : RemoteMediator<Int, CharacterModel>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterModel>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val charactersPage = service.getGetListOfCharactersByPage(
                page = loadKey
            )

            val characters = charactersPage.results

            db.withTransaction {
                characters.forEach {
                    db.characterDao.upsertCharacter(it.toEntity())
                }
            }

            MediatorResult.Success(
                endOfPaginationReached = characters.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}