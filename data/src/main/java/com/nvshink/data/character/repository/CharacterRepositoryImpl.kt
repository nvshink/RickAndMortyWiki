package com.nvshink.data.character.repository

import android.util.Log
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.network.response.CharacterResponse
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.character.utils.CharacterMapper
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.data.location.utils.LocationMapper
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl @Inject constructor(
    private val dao: CharacterDao,
    private val service: CharacterService
) : CharacterRepository {
    /**
     * Get list of characters and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    override suspend fun getCharacters(
        pageInfoModel: PageInfoModel?,
        filterModel: CharacterFilterModel
    ): Flow<Resource<Pair<PageInfoModel?, List<CharacterModel>>>> = flow {
        emit(Resource.Loading)
        try {
            //If the function is called, but the query is empty, an empty value is returned.
            var response: PageResponse<CharacterResponse>
            if (pageInfoModel != null) {
                if (pageInfoModel.next != null) {
                    response = service.getGetListOfCharactersByUrl(pageInfoModel.next!!)
                } else {
                    Log.d("TEST", "Res")
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
                response = service.getGetListOfCharactersByParams(
                    name = filterModel.name ?: "",
                    status = when (filterModel.status) {
                        CharacterStatus.ALIVE -> "Alive"
                        CharacterStatus.DEAD -> "Dead"
                        CharacterStatus.UNKNOWN -> "unknown"
                        null -> ""
                    },
                    species = filterModel.species ?: "",
                    type = filterModel.type ?: "",
                    gender = when (filterModel.gender) {
                        CharacterGender.MALE -> "Male"
                        CharacterGender.FEMALE -> "Female"
                        CharacterGender.GENDERLESS -> "Genderless"
                        CharacterGender.UNKNOWN -> "unknown"
                        null -> ""
                    }
                )
            }

            //Separate info and result
            val responseInfo = response.info
            val responseResult = response.results

            val newCharacterPageInfoModel = PageInfoModel(
                next = responseInfo.next,
                prev = responseInfo.prev
            )

            //White result in local DB
            responseResult.forEach {
                dao.upsertCharacter(CharacterMapper.responseToEntity(it))
            }

            // Return characters
            emit(
                Resource.Success(
                    Pair(
                        first = newCharacterPageInfoModel,
                        second = responseResult.map {
                            CharacterMapper.responseToModel(it)
                        }
                    ),
                    isLocal = false
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Characters error: ${e.message}")
            //Try load from cache
            try {
                dao.getCharacters(
                    name = filterModel.name,
                    status = filterModel.status,
                    species = filterModel.species,
                    type = filterModel.type,
                    gender = filterModel.gender,
                ).map {
                    it.map { characterEntity ->
                        CharacterMapper.entityToModel(characterEntity)
                    }
                }.collect { characters ->

                    emit(
                        Resource.Success(
                            data = Pair(
                                first = null,
                                second = characters
                            ),
                            isLocal = true
                        )
                    )
                }
            } catch (dbException: Throwable) {
                Log.d("DATA_LOAD", "Characters db error: ${dbException.message}")
                emit(Resource.Error(dbException, Pair(first = null, second = emptyList())))
            }
        }
    }

    /**
     * Get list of characters by list id.
     * @param ids List of Character id numbers.
     */
    override suspend fun getCharactersByIds(ids: List<Int>): Flow<Resource<List<CharacterModel>>> =
        flow {
            emit(Resource.Loading)
            try {
                var path = ""
                ids.forEach { id -> path += "$id," }
                val response = service.getGetCharactersByPath(path.dropLast(1))
                response.forEach {
                    dao.upsertCharacter(CharacterMapper.responseToEntity(it))
                }
                emit(Resource.Success(response.map {
                    CharacterMapper.responseToModel(it)
                }))
            } catch (e: Exception) {
                Log.d("DATA_LOAD", "Character by ids error: ${e.message}")
                try {
                    val cachedCharacters: MutableList<CharacterModel> = mutableListOf()
                    ids.map {
                        dao.getCharactersById(it).collect { cachedEntity ->
                            val cachedModel = CharacterMapper.entityToModel(cachedEntity)
                            cachedCharacters.add(cachedModel.id, cachedModel)
                        }
                    }
                    emit(
                        Resource.Success(
                            data = cachedCharacters,
                            isLocal = true,
                            onlineException = e
                        )
                    )
                } catch (dbException: Throwable) {
                    Log.d("DATA_LOAD", "Characters by ids db error: ${dbException.message}")
                    emit(Resource.Error(dbException, emptyList()))
                }
            }
        }

    override suspend fun getCharacterById(id: Int): Flow<Resource<CharacterModel>> = flow {
        emit(Resource.Loading)
        try {
            val response = service.getGetCharacterById(id)
            dao.upsertCharacter(CharacterMapper.responseToEntity(response))
            emit(
                Resource.Success(
                    (CharacterMapper.responseToModel(response))
                )
            )
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Character by id error: ${e.message}")
            try {
                dao.getCharactersById(id).collect { cachedEntity ->
                    emit(
                        Resource.Success(
                            data = CharacterMapper.entityToModel(cachedEntity),
                            isLocal = true,
                            onlineException = e
                        )
                    )
                }

            } catch (dbException: Throwable) {
                Log.d("DATA_LOAD", "Characters by id db error: ${dbException.message}")
                emit(Resource.Error(dbException, null))
            }
        }
    }
}