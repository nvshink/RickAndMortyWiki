package com.nvshink.rickandmortywiki.ui.character.state

import androidx.paging.PagingData
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.ui.generic.state.PageListUiState

data class CharacterPageListUiState(
    val characterList: PagingData<CharacterModel> = PagingData.empty(),
    val currentCharacter: CharacterModel? = null,
    val filter: CharacterFilterModel = CharacterFilterModel(
        name = null,
        status = null,
        species = null,
        type = null,
        gender = null
    ),
    override val isShowingFilter: Boolean = false,
    override val isAtTop: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val searchBarText: String = "",
    override val searchBarFiltersText: String = "",
    override val error: Exception? = null
) : PageListUiState<CharacterModel>