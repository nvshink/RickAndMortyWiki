package com.nvshink.domain.character.repository

import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.Resource
import com.nvshink.domain.resource.SortTypes
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    /**
     * Get list of characters and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getCharacters(
        pageInfoModel: PageInfoModel?,
        filterModel: CharacterFilterModel
    ): Flow<Resource<Pair<PageInfoModel?, List<CharacterModel>>>>

    /**
     * Get list of characters by list id.
     * @param ids List of Character id numbers.
     */
    suspend fun getCharactersByIds(ids: List<Int>): Flow<Resource<List<CharacterModel>>>

    /**
     * Get character by id.
     * @param id Character id numbers.
     */
    suspend fun getCharacterById(id: Int): Flow<Resource<CharacterModel>>
}