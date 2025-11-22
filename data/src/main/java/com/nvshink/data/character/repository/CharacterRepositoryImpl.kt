package com.nvshink.data.character.repository

import android.util.Log
import androidx.room.RoomRawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.network.response.CharacterResponse
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.character.utils.CharacterMapper
import com.nvshink.data.generic.network.response.PageResponse
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class CharacterRepositoryImpl @Inject constructor(
    private val dao: CharacterDao,
    private val service: CharacterService
) : CharacterRepository {
    /**
     * Get list of characters and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    override suspend fun getCharactersApi(
        pageInfoModel: PageInfoModel,
        filterModel: CharacterFilterModel
    ): Flow<Pair<PageInfoModel, Resource<List<CharacterModel>>>> = flow {
        emit(Pair(pageInfoModel, Resource.Loading))
        try {
            //If the function is called, but the query is empty, an empty value is returned.
            var response: PageResponse<CharacterResponse>
            if (pageInfoModel.next != null) {
                response = service.getGetListOfCharactersByUrl(pageInfoModel.next!!)
            } else if (pageInfoModel.prev == null) {
                response = service.getGetListOfCharactersByParams(
                    name = filterModel.name ?: "",
                    status = filterModel.status?.name ?: "",
                    species = filterModel.species ?: "",
                    type = filterModel.type ?: "",
                    gender = filterModel.gender?.name ?: ""
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
                Pair(
                    first = newCharacterPageInfoModel,
                    second = Resource.Success(
                        responseResult.map {
                            CharacterMapper.responseToModel(it)
                        }
                    )
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Characters error: ${e.message}")
            emit(Pair(pageInfoModel, Resource.Error(exception = e)))
        }
    }

    /**
     * Get list of characters by list ids from API.
     * @param ids List of Character id numbers.
     */
    override suspend fun getCharactersByIdsApi(ids: List<Int>): Flow<Resource<List<CharacterModel>>> =
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
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.d("DATA_LOAD", "Character by ids error: ${e.message}")
                emit(Resource.Error(exception = e))
            }
        }

    /**
     * Get characters by id from API.
     * @param id Character id number.
     */
    override suspend fun getCharacterByIdApi(id: Int): Flow<Resource<CharacterModel>> = flow {
        emit(Resource.Loading)
        try {
            val response = service.getGetCharacterById(id)
            dao.upsertCharacter(CharacterMapper.responseToEntity(response))
            emit(
                Resource.Success(
                    (CharacterMapper.responseToModel(response))
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.d("DATA_LOAD", "Character by id error: ${e.message}")
            emit(Resource.Error(exception = e))
        }
    }

    override suspend fun getCharactersDB(
        filterModel: CharacterFilterModel
    ): Flow<Resource<List<CharacterModel>>> = flow {
        emit(Resource.Loading)
        //Try load from cache
        try {
            dao.getCharacters(
                query = sqlQueryBuilder(
                    name = filterModel.name,
                    status = filterModel.status?.name,
                    species = filterModel.species,
                    type = filterModel.type,
                    gender = filterModel.gender?.name
                )
            ).map {
                it.map { characterEntity ->
                    CharacterMapper.entityToModel(characterEntity)
                }
            }.collect { characters ->
                emit(
                    Resource.Success(
                        data = characters
                    )
                )
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.d("DATA_LOAD", "Characters db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    override suspend fun getCharactersByIdsDB(ids: List<Int>): Flow<Resource<List<CharacterModel>>> =
        flow {
            emit(Resource.Loading)
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
                        data = cachedCharacters
                    )
                )
            } catch (ce: CancellationException) {
                throw ce
            } catch (dbException: Exception) {
                Log.d("DATA_LOAD", "Characters by ids db error: ${dbException.message}")
                emit(Resource.Error(exception = dbException))
            }
        }

    override suspend fun getCharacterByIdDB(id: Int): Flow<Resource<CharacterModel>> = flow {
        try {
            dao.getCharactersById(id).collect { cachedEntity ->
                emit(
                    Resource.Success(
                        data = CharacterMapper.entityToModel(cachedEntity)
                    )
                )
            }

        } catch (ce: CancellationException) {
            throw ce
        } catch (dbException: Exception) {
            Log.d("DATA_LOAD", "Characters by id db error: ${dbException.message}")
            emit(Resource.Error(exception = dbException))
        }
    }

    private fun sqlQueryBuilder(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): RoomRawQuery {
        val selectionArgs = mutableListOf<String>()

        if (name != null) {
            selectionArgs.add("name LIKE '%$name%'")
        }

        if (status != null) {
            selectionArgs.add("status LIKE '$status'")
        }

        if (species != null) {
            selectionArgs.add("species LIKE '%$species%'")
        }

        if (type != null) {
            selectionArgs.add("type LIKE '%$type%'")
        }

        if (gender != null) {
            selectionArgs.add("gender LIKE '$gender'")
        }

        val query =
            "SELECT * FROM characters ${if (selectionArgs.isNotEmpty()) "WHERE" else ""} ${selectionArgs.joinToString(" AND ")}"
        return RoomRawQuery(query)
    }

}