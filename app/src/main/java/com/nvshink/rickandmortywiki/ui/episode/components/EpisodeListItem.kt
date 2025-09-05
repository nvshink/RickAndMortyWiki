package com.nvshink.rickandmortywiki.ui.episode.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.location.model.LocationModel

@Composable
fun EpisodeListItem(episode: EpisodeModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(episode.name)
            Row {
                Text(episode.airDate.toString())
            }
        }
    }
}