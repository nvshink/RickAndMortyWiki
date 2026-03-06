package com.nvshink.rickandmortywiki.ui.character.event

import androidx.paging.compose.LazyPagingItems
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface CharacterPageListEvent {
    data class RefreshList(val characters: LazyPagingItems<CharacterModel>) : CharacterPageListEvent
    data object ShowFilterDialog : CharacterPageListEvent
    data object HideFilterDialog : CharacterPageListEvent
    data class SetFilter(val filter: CharacterFilterModel) : CharacterPageListEvent
    data class SetUiStateFilter(val filter: CharacterFilterModel) : CharacterPageListEvent
    data object ClearFilterUi : CharacterPageListEvent
    data class SetContentType(val contentType: ContentType) : CharacterPageListEvent
    data class SetSearchBarText(val text: String) : CharacterPageListEvent
    data class SetIsLocal(val isLocal: Boolean) : CharacterPageListEvent
    data class RetryPageLoad(val characters: LazyPagingItems<CharacterModel>) : CharacterPageListEvent

}