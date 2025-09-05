package com.nvshink.data.character.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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
     * @param sortBy Specifies which field the list will be sorted by
     * @param name The value for filtering by the `name` field. If `null`, it is not filtered.
     * @param status The value for filtering by the `status` field. If `null`, it is not filtered.
     * @param species The value for filtering by the `species` field. If `null`, it is not filtered.
     * @param type The value for filtering by the `type` field. If `null`, it is not filtered.
     * @param gender The value for filtering by the `gender` field. If `null`, it is not filtered.
     */
    @Query(
        "SELECT * FROM characters WHERE (:name IS NULL OR name = :name) " +
                "AND (:status IS NULL OR status = :status) " +
                "AND (:species IS NULL OR species = :species) " +
                "AND (:type IS NULL OR type = :type) " +
                "AND (:gender IS NULL OR gender = :gender) "
    )
    fun getCharacters(
        name: String? = null,
        status: CharacterStatus? = null,
        species: String? = null,
        type: String? = null,
        gender: CharacterGender? = null
    ): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharactersById(
        id: Int
    ): Flow<CharacterEntity>
}