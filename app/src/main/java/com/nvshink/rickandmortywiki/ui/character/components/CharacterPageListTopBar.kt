package com.nvshink.rickandmortywiki.ui.character.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.domain.character.utils.CharacterSortFields
import com.nvshink.domain.resource.SortTypes
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterPageListTopBar(
    uiState: CharacterPageListUiState,
    onEvent: (CharacterPageListEvent) -> Unit
) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                SearchBar(
                    modifier = Modifier.weight(1f),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = uiState.searchBarText,
                            onQueryChange = {
                                onEvent(
                                    CharacterPageListEvent.SetSearchBarText(text = it)
                                )
                            },
                            onSearch = {
                                onEvent(
                                    CharacterPageListEvent.SetFilter(
                                        filter = uiState.filter.copy(
                                            name = it
                                        )
                                    )
                                )
                            },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text(stringResource(R.string.searchbar_placeholder_character)) })
                    },
                    expanded = false,
                    onExpandedChange = {},
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) { }
                Button(
                    onClick = { onEvent(CharacterPageListEvent.ShowFilterDialog) },
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onEvent(
                            CharacterPageListEvent.SetSortType(
                                sortType = when (uiState.sortType) {
                                    SortTypes.ASCENDING -> SortTypes.DESCENDING
                                    SortTypes.DESCENDING -> SortTypes.ASCENDING
                                    else -> SortTypes.ASCENDING
                                }
                            )
                        )
                    },
                ) {
                    Row {
                        Icon(
                            imageVector = when (uiState.sortType) {
                                SortTypes.ASCENDING -> Icons.Default.KeyboardDoubleArrowUp
                                SortTypes.DESCENDING -> Icons.Default.KeyboardDoubleArrowDown
                                SortTypes.NONE -> Icons.Default.QuestionMark
                            },
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.size(10.dp))
                SingleChoiceSegmentedButtonRow {
                    CharacterSortFields.entries.forEachIndexed { index, it ->
                        SegmentedButton(selected = uiState.sortFields == it, onClick = {
                                onEvent(
                                    CharacterPageListEvent.SetSortFields(it)
                                )
                            }, shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = CharacterSortFields.entries.size
                            )) { Text(it.name, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
            if (uiState is CharacterPageListUiState.SuccessState && uiState.isLocal) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.is_local_data_title),
                        modifier = Modifier
                            .clip(
                                MaterialTheme.shapes.extraLarge
                            )
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(5.dp)
                        ,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondaryContainer)
                    )
                }
            }
        }
    }