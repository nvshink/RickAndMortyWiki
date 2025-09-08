package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListCardContent
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListTopBar
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterDetailViewModel
import com.nvshink.rickandmortywiki.ui.generic.components.filter.FilterDialog
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListItem
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListOfItems
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreenColors
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.screen.LocationItemScreen
import com.nvshink.rickandmortywiki.ui.location.viewmodel.LocationDetailViewModel
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.LocationItemScreenRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CharacterScreen(
    modifier: Modifier = Modifier,
    characterPageListUiState: CharacterPageListUiState,
    onCharacterListEvent: (CharacterPageListEvent) -> Unit,
    detailModifier: Modifier,
    contentType: ContentType,
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val coroutineScope = rememberCoroutineScope()
    val detail = @Composable
    fun(character: CharacterModel, onBack: () -> Unit) {

    }
    ListOfItems(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 5.dp),
        listOfItems = characterPageListUiState.characterList,
        detail = { id, onBack ->
            NavHost(
                navController = navController,
                startDestination = CharacterItemScreenRoute(id)
            ) {
                composable<CharacterItemScreenRoute> { nav ->
                    val characterDetailViewModel: CharacterDetailViewModel = hiltViewModel()
                    val characterDetailUiState =
                        characterDetailViewModel.uiState.collectAsState().value
                    val onCharacterDetailEvent = characterDetailViewModel::onEvent
                    val args = nav.toRoute<CharacterItemScreenRoute>()
                    onCharacterDetailEvent(CharacterDetailEvent.SetCharacter(args.id))
                    CharacterItemScreen(
                        modifier = detailModifier,
                        detailUiState = characterDetailUiState,
                        onBackPressed = {
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {
                                onBack()
                            }
                        },
                        navController = navController,
                        contentType = contentType
                    )
                }
                composable<LocationItemScreenRoute> { nav ->
                    val locationDetailViewModel: LocationDetailViewModel = hiltViewModel()
                    val locationDetailUiState =
                        locationDetailViewModel.uiState.collectAsState().value
                    val onLocationDetailEvent = locationDetailViewModel::onEvent
                    val args = nav.toRoute<LocationItemScreenRoute>()
                    onLocationDetailEvent(LocationDetailEvent.SetLocation(args.id))
                    LocationItemScreen(
                        modifier = detailModifier,
                        detailUiState = locationDetailUiState,
                        onBackPressed = {
                            navController.popBackStack()
                        },
                        navController = navController,
                        contentType = contentType
                    )
                }
            }
        },
        scaffoldNavigator = scaffoldNavigator,
        isLoading = characterPageListUiState::class == CharacterPageListUiState.LoadingState::class,
        isRefreshing = characterPageListUiState.isRefreshing,
        onRefresh = { onCharacterListEvent(CharacterPageListEvent.RefreshList) },
        errorMessage = if (characterPageListUiState is CharacterPageListUiState.ErrorState) characterPageListUiState.error?.message else null,
        emptyListTitle = stringResource(R.string.empty_list_title_characters),
        emptyListIcon = Icons.Filled.SearchOff,
        emptyListIconDescription = stringResource(R.string.empty_list_icon_description_characters),
        emptyDetailTitle = stringResource(R.string.empty_screen_title_character),
        emptyDetailIcon = Icons.Filled.Person,
        emptyDetailIconDescription = stringResource(R.string.empty_screen_icon_description_character),
        listArrangement = 10.dp,
        listItem = { character ->
            ListItem(
                onCardClick = {
                    coroutineScope.launch {
                        scaffoldNavigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            character.id
                        )
                    }
                }
            ) {
                CharacterPageListCardContent(character)

            }
        },
        onLoadMore = { onCharacterListEvent(CharacterPageListEvent.LoadMore) },
        listTopContent = {
            CharacterPageListTopBar(
                uiState = characterPageListUiState,
                onEvent = onCharacterListEvent
            )
        },
        colors = EmptyItemScreenColors(
            iconTintColor = MaterialTheme.colorScheme.onSurface,
            textColor = MaterialTheme.colorScheme.outline
        ),
        fab = null
    )

    Box(modifier = modifier) {
        if (characterPageListUiState.isShowingFilter) FilterDialog(
            onDismissRequest = { onCharacterListEvent(CharacterPageListEvent.HideFilterDialog) },
            onReset = { onCharacterListEvent(CharacterPageListEvent.ClearFilterSelection) },
            onConfirm = {
                onCharacterListEvent(
                    CharacterPageListEvent.SetFilter(
                        characterPageListUiState.filter
                    )
                )
            },
            contentType = contentType,
        ) {
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.character_status_filter_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
            )
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.character_gender_filter_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
}