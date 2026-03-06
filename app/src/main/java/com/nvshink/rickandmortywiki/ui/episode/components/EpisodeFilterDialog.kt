package com.nvshink.rickandmortywiki.ui.episode.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodePageListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.filter.FilterDialog
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@Composable
fun EpisodeFilterDialog(
    episodePageListUiState: EpisodePageListUiState,
    onEpisodeListEvent: (EpisodePageListEvent) -> Unit,
    contentType: ContentType
) {
    FilterDialog(
        onDismissRequest = { onEpisodeListEvent(EpisodePageListEvent.HideFilterDialog) },
        onReset = { onEpisodeListEvent(EpisodePageListEvent.ClearFilterUi) },
        onConfirm = {
            onEpisodeListEvent(
                EpisodePageListEvent.SetFilter(
                    episodePageListUiState.filter
                )
            )
            onEpisodeListEvent(EpisodePageListEvent.HideFilterDialog)
        },
        contentType = contentType,
    ) {
        TextField(
            value = episodePageListUiState.filter.episode ?: "",
            onValueChange = {
                onEpisodeListEvent(
                    EpisodePageListEvent.SetUiStateFilter(
                        episodePageListUiState.filter.copy(
                            episode = it
                        )
                    )
                )
            },
            label = { Text(stringResource(R.string.episode_episode)) },
            placeholder = { Text(stringResource(R.string.episode_episode)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
