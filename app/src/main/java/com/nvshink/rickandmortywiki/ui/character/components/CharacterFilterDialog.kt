package com.nvshink.rickandmortywiki.ui.character.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.filter.FilterDialog
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@Composable
fun CharacterFilterDialog(
    characterPageListUiState: CharacterPageListUiState,
    onCharacterListEvent: (CharacterPageListEvent) -> Unit,
    contentType: ContentType
) {
    FilterDialog(
        onDismissRequest = { onCharacterListEvent(CharacterPageListEvent.HideFilterDialog) },
        onReset = { onCharacterListEvent(CharacterPageListEvent.ClearFilterUi) },
        onConfirm = {
            onCharacterListEvent(
                CharacterPageListEvent.SetFilter(
                    characterPageListUiState.filter
                )
            )
            onCharacterListEvent(
                CharacterPageListEvent.RefreshList
            )
            onCharacterListEvent(CharacterPageListEvent.HideFilterDialog)
        },
        contentType = contentType,
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.Start,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.character_status_filter_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.status == CharacterStatus.ALIVE,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    status = CharacterStatus.ALIVE
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_status_alive))
            }
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.status == CharacterStatus.DEAD,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    status = CharacterStatus.DEAD
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_status_dead))
            }
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.status == CharacterStatus.UNKNOWN,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    status = CharacterStatus.UNKNOWN
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_status_unknown))
            }
        }
        TextField(
            value = characterPageListUiState.filter.species ?: "",
            onValueChange = {
                onCharacterListEvent(
                    CharacterPageListEvent.SetUiStateFilter(
                        characterPageListUiState.filter.copy(
                            species = it
                        )
                    )
                )
            },
            label = { Text(stringResource(R.string.character_species_filter_title)) },
            placeholder = { Text(stringResource(R.string.character_species_filter_title)) },
            modifier = Modifier.Companion.fillMaxWidth()
        )
        TextField(
            value = characterPageListUiState.filter.type ?: "",
            onValueChange = {
                onCharacterListEvent(
                    CharacterPageListEvent.SetUiStateFilter(
                        characterPageListUiState.filter.copy(
                            type = it
                        )
                    )
                )
            },
            label = { Text(stringResource(R.string.character_type_filter_title)) },
            placeholder = { Text(stringResource(R.string.character_type_filter_title)) },
            modifier = Modifier.Companion.fillMaxWidth()
        )
        Column(
            horizontalAlignment = Alignment.Companion.Start,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.character_gender_filter_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.gender == CharacterGender.MALE,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    gender = CharacterGender.MALE
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_gender_male))
            }
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.gender == CharacterGender.FEMALE,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    gender = CharacterGender.FEMALE
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_gender_female))
            }
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.gender == CharacterGender.GENDERLESS,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    gender = CharacterGender.GENDERLESS
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_gender_genderless))
            }
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                RadioButton(
                    selected = characterPageListUiState.filter.gender == CharacterGender.UNKNOWN,
                    onClick = {
                        onCharacterListEvent(
                            CharacterPageListEvent.SetUiStateFilter(
                                characterPageListUiState.filter.copy(
                                    gender = CharacterGender.UNKNOWN
                                )
                            )
                        )
                    })
                Text(stringResource(R.string.character_gender_unknown))
            }
        }
    }
}