package com.nvshink.rickandmortywiki.ui.character.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.filter.FilterDialog
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@Composable
private fun FilterRadioButtonRow(
    textRes: Int,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable(onClick = onSelect).fillMaxWidth()
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Text(stringResource(textRes))
    }
}

@Composable
private fun CharacterFilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        placeholder = { Text(stringResource(labelRes)) },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun CharacterFilterDialog(
    characterPageListUiState: CharacterPageListUiState,
    onCharacterListEvent: (CharacterPageListEvent) -> Unit,
    contentType: ContentType
) {
    val filter = characterPageListUiState.filter
    val onFilterChange: (CharacterFilterModel.() -> CharacterFilterModel) -> Unit = { update ->
        onCharacterListEvent(CharacterPageListEvent.SetUiStateFilter(filter.update()))
    }

    FilterDialog(
        onDismissRequest = { onCharacterListEvent(CharacterPageListEvent.HideFilterDialog) },
        onReset = { onCharacterListEvent(CharacterPageListEvent.ClearFilterUi) },
        onConfirm = {
            onCharacterListEvent(CharacterPageListEvent.SetFilter(filter))
            onCharacterListEvent(CharacterPageListEvent.HideFilterDialog)
        },
        contentType = contentType,
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.character_status_filter_title),
                style = MaterialTheme.typography.headlineSmall
            )
            FilterRadioButtonRow(
                textRes = R.string.character_status_alive,
                selected = filter.status == CharacterStatus.ALIVE,
                onSelect = { onFilterChange { copy(status = CharacterStatus.ALIVE) } }
            )
            FilterRadioButtonRow(
                textRes = R.string.character_status_dead,
                selected = filter.status == CharacterStatus.DEAD,
                onSelect = { onFilterChange { copy(status = CharacterStatus.DEAD) } }
            )
            FilterRadioButtonRow(
                textRes = R.string.character_status_unknown,
                selected = filter.status == CharacterStatus.UNKNOWN,
                onSelect = { onFilterChange { copy(status = CharacterStatus.UNKNOWN) } }
            )
        }
        CharacterFilterTextField(
            value = filter.species ?: "",
            onValueChange = { onFilterChange { copy(species = it) } },
            labelRes = R.string.character_species_filter_title
        )
        CharacterFilterTextField(
            value = filter.type ?: "",
            onValueChange = { onFilterChange { copy(type = it) } },
            labelRes = R.string.character_type_filter_title
        )
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.character_gender_filter_title),
                style = MaterialTheme.typography.headlineSmall
            )
            FilterRadioButtonRow(
                textRes = R.string.character_gender_male,
                selected = filter.gender == CharacterGender.MALE,
                onSelect = { onFilterChange { copy(gender = CharacterGender.MALE) } }
            )
            FilterRadioButtonRow(
                textRes = R.string.character_gender_female,
                selected = filter.gender == CharacterGender.FEMALE,
                onSelect = { onFilterChange { copy(gender = CharacterGender.FEMALE) } }
            )
            FilterRadioButtonRow(
                textRes = R.string.character_gender_genderless,
                selected = filter.gender == CharacterGender.GENDERLESS,
                onSelect = { onFilterChange { copy(gender = CharacterGender.GENDERLESS) } }
            )
            FilterRadioButtonRow(
                textRes = R.string.character_gender_unknown,
                selected = filter.gender == CharacterGender.UNKNOWN,
                onSelect = { onFilterChange { copy(gender = CharacterGender.UNKNOWN) } }
            )
        }
    }
}
