package com.nvshink.rickandmortywiki.ui.character.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterLocationModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeSmallListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.SmallListOfItems
import com.nvshink.rickandmortywiki.ui.location.screen.CharacterSmallListItem
import com.nvshink.rickandmortywiki.ui.location.screen.EpisodeSmallListItem
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.EpisodeItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.LocationItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.getIcon
import com.nvshink.rickandmortywiki.ui.utils.getName
import java.time.ZonedDateTime

@Composable
fun CharacterItemViewScreen(
    modifier: Modifier = Modifier,
    character: CharacterModel,
    episodesUiState: EpisodeSmallListUiState,
    onSmallListRefresh: () -> Unit = {},
    onNavigation: (Any) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        //Image
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(10.dp)
            ) {
                AsyncImage(
                    character.image, contentDescription = null, modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.large
                        )
                        .size(200.dp)
                )
            }
        }
        //Name
        Text(
            character.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    //Status
                    TextDetailProperty(
                        label = stringResource(R.string.character_status_filter_title),
                        leadingIcon = {
                            Icon(
                                imageVector = character.status.getIcon(),
                                contentDescription = null
                            )
                        },
                        content = character.status.getName(context = context)
                    )
                    //Species
                    TextDetailProperty(
                        label = stringResource(R.string.character_species_filter_title),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Accessibility,
                                contentDescription = null
                            )
                        },
                        content = character.species
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    //Type
                    TextDetailProperty(
                        label = stringResource(R.string.character_type_filter_title),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null
                            )
                        },
                        content = character.type
                    )
                    //Gender
                    TextDetailProperty(
                        label = stringResource(R.string.character_gender_filter_title),
                        leadingIcon = {
                            Icon(
                                imageVector = character.gender.getIcon(),
                                contentDescription = null
                            )
                        },
                        content = character.gender.getName(context = context)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                //Location
                if (character.location.id != null) {
                    TextButtonDetailProperty(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.character_location_title),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        content = character.location.name,
                        onClick = {
                            onNavigation(LocationItemScreenRoute(character.location.id!!))
                        }
                    )
                }
                //Origin
                if (character.origin.id != null) {
                    TextButtonDetailProperty(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.character_origin_title),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null
                            )
                        },
                        content = character.origin.name,
                        onClick = {
                            onNavigation(LocationItemScreenRoute(character.origin.id!!))
                        }
                    )
                }
            }
            //Episodes
            SmallListOfItems(
                title = stringResource(R.string.character_episodes_title),
                isLoading = episodesUiState is EpisodeSmallListUiState.LoadingState,
                errorMessage = if (episodesUiState is EpisodeSmallListUiState.ErrorState) episodesUiState.error?.message
                    ?: "" else null,
                onRetryClick = onSmallListRefresh,
                listOfItems = if (episodesUiState is EpisodeSmallListUiState.SuccessState) episodesUiState.episodeList else emptyList(),
                listItem = { episodeModel ->
                    EpisodeSmallListItem(
                        name = episodeModel.name,
                        episode = episodeModel.episode,
                        onClick = {
                            onNavigation(EpisodeItemScreenRoute(episodeModel.id))
                        })
                }
            )
        }
    }
}

@Composable
fun TextDetailProperty(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: @Composable () -> Unit = {},
    content: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box { leadingIcon() }
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun TextButtonDetailProperty(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: @Composable () -> Unit = {},
    content: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small
    ) { TextDetailProperty(label = label, leadingIcon = leadingIcon, content = content) }
}