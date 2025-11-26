package com.nvshink.rickandmortywiki.ui.episode.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.R

@Composable
fun EpisodePageListCardContent(episode: EpisodeModel) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        Text(episode.name, style = MaterialTheme.typography.titleMedium)
        Text("${episode.episode}, ${stringResource(R.string.episode_on_air)}: ${episode.airDate}", style = MaterialTheme.typography.titleSmall)
    }
}