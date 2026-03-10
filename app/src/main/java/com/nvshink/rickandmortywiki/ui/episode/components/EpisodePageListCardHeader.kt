package com.nvshink.rickandmortywiki.ui.episode.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.episode.model.EpisodeUiModel

@Composable
fun EpisodePageListCardHeader(season: String) {
    Text(
        text = "${stringResource(R.string.episode_season)} ${season.substring(1).toInt()}",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
    )
}