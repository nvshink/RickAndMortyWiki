package com.nvshink.data.character.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nvshink.data.character.network.service.CharacterService
import com.nvshink.data.character.utils.toModel
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel

class CharacterPagingSource(
    private val service: CharacterService,
    private val filter: CharacterFilterModel = CharacterFilterModel()
) : PagingSource<Int, CharacterModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val currentPage = params.key ?: 1

        return try {
            val response = if (filter.name != null || filter.status != null || 
                filter.species != null || filter.type != null || filter.gender != null) {
                service.getGetListOfCharactersByParams(
                    name = filter.name ?: "",
                    status = filter.status?.name ?: "",
                    species = filter.species ?: "",
                    type = filter.type ?: "",
                    gender = filter.gender?.name ?: ""
                )
            } else {
                service.getGetListOfCharactersByPage(page = currentPage)
            }
            
            val characters = response.results.map { it.toModel() }

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (characters.isEmpty() || response.info.next == null) null else currentPage + 1
            Log.d("TEST", "Paging source: ${characters.size}; prev: ${nextPage}; prev: ${nextPage}")
            LoadResult.Page(
                data = characters,
                prevKey = prevPage,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}