package com.nvshink.data.character.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.room.RoomRawQuery
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.network.response.CharacterResponse
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.character.paging.CharacterLocalPagingSource
import com.nvshink.data.character.paging.CharacterPagingSource
import com.nvshink.data.character.paging.CharacterRemoteMediator
import com.nvshink.data.character.utils.toEntity
import com.nvshink.data.character.utils.toModel
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.data.generic.network.exception.ResourceNotFoundException
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
    private val service: CharacterService,
    private val database: RickAndMortyWikiDB
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharactersStream(
        filterModel: CharacterFilterModel
    ): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(
                database = database,
                dao = dao,
                service = service
            ),
            pagingSourceFactory = { CharacterLocalPagingSource(dao) }
        ).flow.map { pagingData -> pagingData.map { entity -> entity.toModel() } }
    }
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
                dao.upsertCharacter(it.toEntity())
            }

            // Return characters
            emit(
                Pair(
                    first = newCharacterPageInfoModel,
                    second = Resource.Success(
                        responseResult.map {
                            it.toModel()
                        }
                    )
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.d("DATA_LOAD", "Characters error: ${resourceNotFound.message}")
            emit(
                Pair(
                    first = pageInfoModel,
                    second = Resource.Success(
                        emptyList()
                    )
                )
            )
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
                val response = service.getGetListOfCharactersByPath(path)
                response.forEach {
                    dao.upsertCharacter(it.toEntity())
                }
                emit(Resource.Success(response.map {
                    it.toModel()
                }))
            } catch (ce: CancellationException) {
                throw ce
            } catch (resourceNotFound: ResourceNotFoundException) {
                Log.d("DATA_LOAD", "Characters by ids error: ${resourceNotFound.message}")
                emit(
                    Resource.Success(
                        emptyList()
                    )
                )
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
            dao.upsertCharacter(response.toEntity())
            emit(
                Resource.Success(
                    (response.toModel())
                )
            )
        } catch (ce: CancellationException) {
            throw ce
        } catch (resourceNotFound: ResourceNotFoundException) {
            Log.d("DATA_LOAD", "Character by id error: ${resourceNotFound.message}")
            emit(
                Resource.Error(
                    exception = resourceNotFound
                )
            )
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
//            dao.getCharacters(
//                query = sqlCharacterQueryBuilder(
//                    name = filterModel.name,
//                    status = filterModel.status?.name,
//                    species = filterModel.species,
//                    type = filterModel.type,
//                    gender = filterModel.gender?.name
//                )
//            ).map {
//                it.map { characterEntity ->
//                    characterEntity.toModel()
//                }
//            }.collect { characters ->
//                emit(
//                    Resource.Success(
//                        data = characters
//                    )
//                )
//            }
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
                dao.getCharactersByIds(ids).map { characters ->
                    characters.map { it.toModel() }
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
                Log.d("DATA_LOAD", "Characters by ids db error: ${dbException.message}")
                emit(Resource.Error(exception = dbException))
            }
        }

    override suspend fun getCharacterByIdDB(id: Int): Flow<Resource<CharacterModel>> = flow {
        try {
            dao.getCharactersById(id).collect { cachedEntity ->
                emit(
                    Resource.Success(
                        data = cachedEntity.toModel()
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

    private fun sqlCharacterQueryBuilder(
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
            "SELECT * FROM characters ${if (selectionArgs.isNotEmpty()) "WHERE" else ""} ${
                selectionArgs.joinToString(
                    " AND "
                )
            }"
        return RoomRawQuery(query)
    }

}