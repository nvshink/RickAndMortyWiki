package com.nvshink.data.character.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    /**
     * Save new and update existing character
     */
    @Upsert
    suspend fun upsertCharacter(characterEntity: CharacterEntity)

    /**
     * Return list of characters to the entered conditions
     */
    @RawQuery(observedEntities = [CharacterEntity::class])
    fun getCharacters(
        query: RoomRawQuery
    ): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharactersById(
        id: Int
    ): Flow<CharacterEntity>
}