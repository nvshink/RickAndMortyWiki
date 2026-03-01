package com.nvshink.data.character.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.data.character.local.entity.CharacterRemoteKey
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Upsert
    suspend fun upsertCharacters(characters: List<CharacterEntity>)

    /**
     * Save new and update existing character
     */
    @Upsert
    suspend fun upsertCharacter(characterEntity: CharacterEntity)

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharactersById(
        id: Int
    ): Flow<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id IN (:ids)")
    fun getCharactersByIds(
        ids: List<Int>
    ): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getCharactersPaging(limit: Int, offset: Int): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getCharactersPagingList(limit: Int, offset: Int): List<CharacterEntity>

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharactersCount(): Int

    @Query("DELETE FROM characters")
    suspend fun clearCharacters()

    @Upsert
    suspend fun upsertRemoteKeys(remoteKeys: List<CharacterRemoteKey>)

    @Query("SELECT * FROM character_remote_keys WHERE characterId = :characterId")
    suspend fun getRemoteKeyByCharacterId(characterId: Int): CharacterRemoteKey?

    @Query("DELETE FROM character_remote_keys")
    suspend fun clearRemoteKeys()
}