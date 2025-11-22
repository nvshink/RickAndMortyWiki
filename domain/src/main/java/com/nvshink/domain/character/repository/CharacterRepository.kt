package com.nvshink.domain.character.repository

import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    /**
     * Get list of characters and page from API:
     * @param pageInfoModel Current page information.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getCharactersApi(
        pageInfoModel: PageInfoModel,
        filterModel: CharacterFilterModel
    ): Flow<Pair<PageInfoModel, Resource<List<CharacterModel>>>>

    /**
     * Get list of characters by list id from API.
     * @param ids List of Character id numbers.
     */
    suspend fun getCharactersByIdsApi(ids: List<Int>): Flow<Resource<List<CharacterModel>>>

    /**
     * Get character by id from API.
     * @param id Character id numbers.
     */
    suspend fun getCharacterByIdApi(id: Int): Flow<Resource<CharacterModel>>

    /**
     * Get list of characters from DB:
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getCharactersDB(
        filterModel: CharacterFilterModel
    ): Flow<Resource<List<CharacterModel>>>

    /**
     * Get list of characters by list id from DB.
     * @param ids List of Character id numbers.
     */
    suspend fun getCharactersByIdsDB(ids: List<Int>): Flow<Resource<List<CharacterModel>>>

    /**
     * Get character by id from DB.
     * @param id Character id numbers.
     */
    suspend fun getCharacterByIdDB(id: Int): Flow<Resource<CharacterModel>>
}