package com.nvshink.data.character.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.data.character.utils.toModel

class CharacterLocalPagingSource(
    private val dao: CharacterDao
) : PagingSource<Int, CharacterEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            val characters = dao.getCharactersPagingList(
                limit = pageSize,
                offset = offset
            )
            LoadResult.Page(
                data = characters,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (characters.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
