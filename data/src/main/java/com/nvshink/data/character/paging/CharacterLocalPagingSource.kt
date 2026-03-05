package com.nvshink.data.character.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.character.local.entity.CharacterEntity
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.data.character.utils.toModel
import kotlinx.coroutines.delay

class CharacterLocalPagingSource(
    private val dao: CharacterDao
) : PagingSource<Int, CharacterEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = if (page == 0) 0 else (page - 1) * pageSize
            Log.d("ROOM", "key: ${params.key}, page: ${page}, limit: ${pageSize + offset}, offset: $offset")
            val characters = dao.getCharactersPagingList(
                limit = pageSize + offset,
                offset = offset
            )
//            val characters = dao.getCharacters()
            Log.d("ROOM", "Paging Source characters: ${characters.size}")
            LoadResult.Page(
                data = characters,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (characters.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("PagingSource", "Character PagingSource error: ${e.message}")
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
