package com.nvshink.rickandmortywiki.ui.character.event

import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.ui.utils.ContentType

sealed interface CharacterPageListEvent {
    data object ShowList : CharacterPageListEvent
    data object HideList : CharacterPageListEvent
    data object RefreshList: CharacterPageListEvent
    data object ShowSortDropDown: CharacterPageListEvent
    data object HideSortDropDown: CharacterPageListEvent
    data object ShowFilterDialog: CharacterPageListEvent
    data object HideFilterDialog: CharacterPageListEvent
    data object ShowToTopButton: CharacterPageListEvent
    data object HideToTopButton: CharacterPageListEvent
    data object ClearFilterSelection: CharacterPageListEvent
    data class SetFilter(val filter: CharacterFilterModel): CharacterPageListEvent
    data class SetUiStateFilter(val filter: CharacterFilterModel): CharacterPageListEvent
    data object LoadMore: CharacterPageListEvent
    data class UpdateCurrentCharacter(val character: CharacterModel?) : CharacterPageListEvent
    data class SetSortType(val sortType: SortTypes) : CharacterPageListEvent
    data class SetSortFields(val sortFields: CharacterSortFields) : CharacterPageListEvent
    data class SetContentType(val contentType: ContentType) : CharacterPageListEvent
    data class SetSearchBarText (val text: String): CharacterPageListEvent

}