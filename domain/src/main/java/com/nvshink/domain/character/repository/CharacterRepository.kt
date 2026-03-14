package com.nvshink.domain.character.repository

import androidx.paging.PagingData
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.resource.Resource
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharactersStream(
        filterModel: CharacterFilterModel
    ): Flow<PagingData<CharacterModel>>

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