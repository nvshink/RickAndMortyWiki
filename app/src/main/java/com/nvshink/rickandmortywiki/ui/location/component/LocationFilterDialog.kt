package com.nvshink.rickandmortywiki.ui.location.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.generic.components.filter.FilterDialog
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationPageListUiState
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@Composable
fun LocationFilterDialog(
    locationPageListUiState: LocationPageListUiState,
    onLocationListEvent: (LocationPageListEvent) -> Unit,
    contentType: ContentType
) {
    FilterDialog(
        onDismissRequest = { onLocationListEvent(LocationPageListEvent.HideFilterDialog) },
        onReset = { onLocationListEvent(LocationPageListEvent.ClearFilterUi) },
        onConfirm = {
            onLocationListEvent(
                LocationPageListEvent.SetFilter(
                    locationPageListUiState.filter
                )
            )
            onLocationListEvent(LocationPageListEvent.HideFilterDialog)
        },
        contentType = contentType,
    ) {
        TextField(
            value = locationPageListUiState.filter.type ?: "",
            onValueChange = {
                onLocationListEvent(
                    LocationPageListEvent.SetUiStateFilter(
                        locationPageListUiState.filter.copy(
                            type = it
                        )
                    )
                )
            },
            label = { Text(stringResource(R.string.location_type)) },
            placeholder = { Text(stringResource(R.string.location_type)) },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = locationPageListUiState.filter.dimension ?: "",
            onValueChange = {
                onLocationListEvent(
                    LocationPageListEvent.SetUiStateFilter(
                        locationPageListUiState.filter.copy(
                            dimension = it
                        )
                    )
                )
            },
            label = { Text(stringResource(R.string.location_dimension)) },
            placeholder = { Text(stringResource(R.string.location_dimension)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
