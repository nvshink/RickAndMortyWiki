package com.nvshink.rickandmortywiki.ui.episode.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.screen.TextDetailProperty
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.SmallListOfItems
import com.nvshink.rickandmortywiki.ui.location.screen.CharacterSmallListItem
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import java.time.format.DateTimeFormatter

@Composable
fun EpisodeItemViewScreen(
    modifier: Modifier = Modifier,
    episode: EpisodeModel,
    charactersUiState: CharacterSmallListUiState,
    onSmallListRefresh: () -> Unit = {},
    onNavigation: (Any) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        //Name
        Text(
            episode.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            //Episode
            TextDetailProperty(
                label = stringResource(R.string.episode_episode),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null
                    )
                },
                content = episode.episode
            )
            //AirDate
            TextDetailProperty(
                label = stringResource(R.string.character_species_filter_title),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null
                    )
                },
                content = episode.airDate.format(DateTimeFormatter.ISO_DATE)
            )
            //Characters
            SmallListOfItems(
                isLoading = charactersUiState is CharacterSmallListUiState.LoadingState,
                errorMessage = if (charactersUiState is CharacterSmallListUiState.ErrorState) charactersUiState.error?.message
                    ?: "" else null,
                onRetryClick = onSmallListRefresh,
                listOfItems = if (charactersUiState is CharacterSmallListUiState.SuccessState) charactersUiState.characterList else emptyList(),
                listItem = { characterModel ->
                    CharacterSmallListItem(
                        name = characterModel.name,
                        onClick = {
                            onNavigation(CharacterItemScreenRoute(characterModel.id))
                        })
                }
            )
        }
    }
}