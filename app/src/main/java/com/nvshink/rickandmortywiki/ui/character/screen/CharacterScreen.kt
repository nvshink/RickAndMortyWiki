package com.nvshink.rickandmortywiki.ui.character.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.components.CharacterPageListCard
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterPageListUiState
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterDetailViewModel
import com.nvshink.rickandmortywiki.ui.generic.components.list.InfinityLazyGrid
import com.nvshink.rickandmortywiki.ui.generic.components.list.ListItem
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
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
    characterScreenModifier: Modifier,
    contentType: ContentType,
    innerPadding: PaddingValues
) {


    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<CharacterModel>()
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()




    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                InfinityLazyGrid(
                    items = characterPageListUiState.characterList,
                    lazyGridState = lazyGridState,
                    cellsArrangement = 5.dp,
                    listItem = { item ->
                        ListItem(
                            onCardClick = {
//                                if (item != characterPageListUiState.currentCharacter) {
//                                    navController.graph.setStartDestination(CharacterItemScreenRoute(item.id))
//                                    navController.navigate(CharacterItemScreenRoute(item.id))
//                                }
                                scope.launch {
                                    scaffoldNavigator.navigateTo(
                                        ListDetailPaneScaffoldRole.Detail,
                                        item
                                    )
                                }
                            }
                        ) {
                            CharacterPageListCard(character = item)
                        }
                    },
                    onLoadMore = { onCharacterListEvent(CharacterPageListEvent.LoadMore) },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                scaffoldNavigator.currentDestination?.contentKey?.let { character ->
                    val navController = rememberNavController()
                    val navHost = remember {
                        movableContentOf<CharacterModel> {
                            NavHost(
                                navController = navController,
                                startDestination = CharacterItemScreenRoute(it.id)
                            ) {
                                composable<CharacterItemScreenRoute> { nav ->
                                    val characterDetailViewModel: CharacterDetailViewModel = hiltViewModel()
                                    val characterDetailUiState = characterDetailViewModel.uiState.collectAsState().value
                                    val onCharacterDetailEvent = characterDetailViewModel::onEvent
                                    val args = nav.toRoute<CharacterItemScreenRoute>()
                                    onCharacterDetailEvent(CharacterDetailEvent.SetCharacter(args.id))
                                    CharacterItemScreen(
                                        detailUiState = characterDetailUiState,
                                        onBackPressed = {
                                            navController.popBackStack(
                                                navController.graph.startDestinationRoute ?: "", false
                                            )
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
                                        detailUiState = locationDetailUiState,
                                        onBackPressed = {
                                            scope.launch { scaffoldNavigator.navigateBack() }
                                        },
                                        contentType = contentType
                                    )
                                }
                            }
                        }
                    }
                    navHost(character)
                } ?: EmptyItemScreen(
                    title = stringResource(R.string.empty_screen_title_character),
                    icon = Icons.Filled.Person,
                    iconDescription = stringResource(R.string.empty_screen_icon_description_character)
                )
            }

        }
    )

//    val listOfItemsCompose = @Composable {
//        ListOfItems(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(horizontal = 5.dp),
//            listOfItems = characterPageListUiState.characterList,
//            isLoading = characterPageListUiState::class == CharacterPageListUiState.LoadingState::class,
//            isRefreshing = characterPageListUiState.isRefreshing,
//            onRefresh = { onCharacterListEvent(CharacterPageListEvent.RefreshList) },
//            errorMessage = if (characterPageListUiState is CharacterPageListUiState.ErrorState) characterPageListUiState.error?.message else null,
//            emptyListIcon = Icons.Filled.SearchOff,
//            emptyListIconDescription = stringResource(R.string.empty_list_icon_description_characters),
//            emptyListTitle = stringResource(R.string.empty_list_title_characters),
//            listArrangement = 10.dp,
//            listItem = { item ->
//                ListItem(
//                    onCardClick = {
//                        onCharacterListEvent(CharacterPageListEvent.UpdateCurrentCharacter(item))
//                        onCharacterListEvent(CharacterPageListEvent.HideList)
//                        navController.navigate(route = CharacterItemScreenRoute(item.id))
//                    }
//                ) {
//                    val genderText: Int
//                    val genderColor: Color
//                    val genderIcon: ImageVector
//                    when (item.gender) {
//                        CharacterGender.MALE -> {
//                            genderText = R.string.character_gender_male
//                            genderColor = Color(0xFF67C3E7)
//                            genderIcon = Icons.Default.Male
//                        }
//
//
//                        CharacterGender.FEMALE -> {
//                            genderText = R.string.character_gender_female
//                            genderColor = Color(0xFFE54F7F)
//                            genderIcon = Icons.Default.Female
//                        }
//
//                        CharacterGender.GENDERLESS -> {
//                            genderText = R.string.character_gender_genderless
//                            genderColor = Color(0xFF36DC65)
//                            genderIcon = Icons.Default.Circle
//                        }
//
//                        CharacterGender.UNKNOWN -> {
//                            genderText = R.string.character_gender_unknown
//                            genderColor = Color(0xFF9D9D9D)
//                            genderIcon = Icons.Default.QuestionMark
//                        }
//                    }
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .size(150.dp),
//                        Alignment.Center
//                    )
//                    {
//                        AsyncImage(
//                            item.image,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .blur(2.dp),
//                            contentScale = ContentScale.FillWidth
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(
//                                        alpha = 0.6f
//                                    )
//                                ),
//                            contentAlignment = Alignment.BottomCenter
//                        ) {
//                            Column(
//                                Modifier
//                                    .fillMaxSize(),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.SpaceAround
//                            ) {
//                                Text(
//                                    text = item.name,
//                                    style = MaterialTheme.typography.titleMedium,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier
//
//                                )
//                                FlowRow(
//                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
//                                    verticalArrangement = Arrangement.spacedBy(5.dp),
//                                    itemVerticalAlignment = Alignment.Bottom
//                                ) {
//                                    Text(
//                                        text = item.species,
//                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
//                                        modifier = Modifier
//                                            .clip(
//                                                CircleShape
//                                            )
//                                            .background(Color.Black)
//                                            .padding(6.dp)
//                                    )
//                                    Row(
//                                        modifier = Modifier
//                                            .clip(
//                                                CircleShape
//                                            )
//                                            .background(genderColor)
//                                            .padding(6.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Text(
//                                            text = stringResource(genderText),
//                                            style = MaterialTheme.typography.bodySmall
//                                        )
//                                        Icon(
//                                            imageVector = genderIcon,
//                                            contentDescription = null,
//                                            modifier = Modifier.size(16.dp)
//                                        )
//                                    }
//                                    Row(
//                                        modifier = Modifier
//                                            .clip(
//                                                CircleShape
//                                            )
//                                            .background(Color.Black)
//                                            .padding(6.dp),
//                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Text(
//                                            text = item.status.name,
//                                            style = MaterialTheme.typography.bodySmall.copy(
//                                                color = Color.White
//                                            ),
//                                        )
//                                        Box(
//                                            Modifier
//                                                .clip(
//                                                    CircleShape
//                                                )
//                                                .size(10.dp)
//                                                .background(
//                                                    when (item.status) {
//                                                        CharacterStatus.ALIVE -> Color.Green
//                                                        CharacterStatus.DEAD -> Color.Red
//                                                        CharacterStatus.UNKNOWN -> Color.Yellow
//                                                    }
//                                                )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            },
//            onLoadMore = { onCharacterListEvent(CharacterPageListEvent.LoadMore) },
//            hideToTopButton = { onCharacterListEvent(CharacterPageListEvent.HideToTopButton) },
//            showToTopButton = { onCharacterListEvent(CharacterPageListEvent.ShowToTopButton) },
//            listTopContent = {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(15.dp)
//                    ) {
//                        SearchBar(
//                            modifier = Modifier.weight(1f),
//                            inputField = {
//                                SearchBarDefaults.InputField(
//                                    query = characterPageListUiState.searchBarText,
//                                    onQueryChange = {
//                                        onCharacterListEvent(
//                                            CharacterPageListEvent.SetSearchBarText(text = it)
//                                        )
//                                    },
//                                    onSearch = {
//                                        onCharacterListEvent(
//                                            CharacterPageListEvent.SetFilter(
//                                                filter = characterPageListUiState.filter.copy(
//                                                    name = it
//                                                )
//                                            )
//                                        )
//                                    },
//                                    expanded = false,
//                                    onExpandedChange = {},
//                                    placeholder = { Text(stringResource(R.string.searchbar_placeholder_character)) })
//                            },
//                            expanded = false,
//                            onExpandedChange = {},
//                            windowInsets = WindowInsets(0, 0, 0, 0),
//                        ) { }
//                        Button(
//                            onClick = { onCharacterListEvent(CharacterPageListEvent.ShowFilterDialog) },
//                            modifier = Modifier.size(48.dp),
//                            contentPadding = PaddingValues(0.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.FilterList,
//                                contentDescription = null
//                            )
//                        }
//                    }
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        Button(
//                            onClick = {
//                                onCharacterListEvent(
//                                    CharacterPageListEvent.SetSortType(
//                                        sortType = when (characterPageListUiState.sortType) {
//                                            SortTypes.ASCENDING -> SortTypes.DESCENDING
//                                            SortTypes.DESCENDING -> SortTypes.ASCENDING
//                                            else -> SortTypes.ASCENDING
//                                        }
//                                    )
//                                )
//                            },
//                        ) {
//                            Row {
//                                Icon(
//                                    imageVector = when (characterPageListUiState.sortType) {
//                                        SortTypes.ASCENDING -> Icons.Default.KeyboardDoubleArrowUp
//                                        SortTypes.DESCENDING -> Icons.Default.KeyboardDoubleArrowDown
//                                        SortTypes.NONE -> Icons.Default.QuestionMark
//                                    },
//                                    contentDescription = null
//                                )
//                            }
//                        }
//                        Spacer(Modifier.size(10.dp))
//                        SingleChoiceSegmentedButtonRow {
//                            CharacterSortFields.entries.forEachIndexed { index, it ->
//                                SegmentedButton(
//                                    selected = characterPageListUiState.sortFields == it,
//                                    onClick = {
//                                        onCharacterListEvent(
//                                            CharacterPageListEvent.SetSortFields(it)
//                                        )
//                                    },
//                                    shape = SegmentedButtonDefaults.itemShape(
//                                        index = index,
//                                        count = CharacterSortFields.entries.size
//                                    )
//                                ) { Text(it.name, style = MaterialTheme.typography.bodySmall) }
//                            }
//                        }
//                    }
//                    if (characterPageListUiState is CharacterPageListUiState.SuccessState && characterPageListUiState.isLocal) {
//                        Box(
//                            modifier = Modifier.fillMaxWidth(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = stringResource(R.string.is_local_data_title),
//                                modifier = Modifier
//                                    .clip(
//                                        MaterialTheme.shapes.extraLarge
//                                    )
//                                    .background(MaterialTheme.colorScheme.secondaryContainer)
//                                    .padding(5.dp)
//                                ,
//                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondaryContainer)
//                            )
//                        }
//                    }
//                }
//            },
//            colors = EmptyItemScreenColors(
//                iconTintColor = MaterialTheme.colorScheme.onSurface,
//                textColor = MaterialTheme.colorScheme.outline
//            ),
//            fab = null
//        )
//    }
//
//    Box(modifier = modifier) {
//        if (characterPageListUiState.isShowingFilter) FilterDialog(
//            onDismissRequest = { onCharacterListEvent(CharacterPageListEvent.HideFilterDialog) },
//            onReset = { onCharacterListEvent(CharacterPageListEvent.ClearFilterSelection) },
//            onConfirm = { onCharacterListEvent(CharacterPageListEvent.SetFilter(characterPageListUiState.filter)) },
//            contentType = contentType,
//        ) {
//            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    stringResource(R.string.character_status_filter_title),
//                    style = MaterialTheme.typography.headlineSmall
//                )
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.status == CharacterStatus.ALIVE,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        status = CharacterStatus.ALIVE
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_status_alive))
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.status == CharacterStatus.DEAD,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        status = CharacterStatus.DEAD
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_status_dead))
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.status == CharacterStatus.UNKNOWN,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        status = CharacterStatus.UNKNOWN
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_status_unknown))
//                }
//            }
//            TextField(
//                value = characterPageListUiState.filter.species ?: "",
//                onValueChange = {
//                    onCharacterListEvent(
//                        CharacterPageListEvent.SetUiStateFilter(characterPageListUiState.filter.copy(species = it))
//                    )
//                },
//                label = { Text(stringResource(R.string.character_species_filter_title)) },
//                placeholder = { Text(stringResource(R.string.character_species_filter_title)) },
//                modifier = Modifier.fillMaxWidth()
//            )
//            TextField(
//                value = characterPageListUiState.filter.type ?: "",
//                onValueChange = {
//                    onCharacterListEvent(
//                        CharacterPageListEvent.SetUiStateFilter(characterPageListUiState.filter.copy(type = it))
//                    )
//                },
//                label = { Text(stringResource(R.string.character_type_filter_title)) },
//                placeholder = { Text(stringResource(R.string.character_type_filter_title)) },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    stringResource(R.string.character_gender_filter_title),
//                    style = MaterialTheme.typography.headlineSmall
//                )
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.gender == CharacterGender.MALE,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        gender = CharacterGender.MALE
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_gender_male))
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.gender == CharacterGender.FEMALE,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        gender = CharacterGender.FEMALE
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_gender_female))
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.gender == CharacterGender.GENDERLESS,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        gender = CharacterGender.GENDERLESS
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_gender_genderless))
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    RadioButton(
//                        selected = characterPageListUiState.filter.gender == CharacterGender.UNKNOWN,
//                        onClick = {
//                            onCharacterListEvent(
//                                CharacterPageListEvent.SetUiStateFilter(
//                                    characterPageListUiState.filter.copy(
//                                        gender = CharacterGender.UNKNOWN
//                                    )
//                                )
//                            )
//                        })
//                    Text(stringResource(R.string.character_gender_unknown))
//                }
//            }
//        }
//        when (contentType) {
//            ContentType.LIST_ONLY -> {
//                if (characterPageListUiState.isShowingList) {
//                    listOfItemsCompose()
//                } else {
//                    BackHandler { onCharacterListEvent(CharacterPageListEvent.ShowList) }
//                }
//                Box(modifier = Modifier.alpha(if (characterPageListUiState.isShowingList) 0f else 1f)) {
//                    navHost(
//                        innerPadding
//                    )
//                }
//            }
//
//            ContentType.LIST_AND_DETAIL -> {
//                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                    Box(modifier = Modifier.weight(1f)) { listOfItemsCompose() }
//                    Box(modifier = Modifier.weight(1f)) { navHost(innerPadding) }
//                }
//            }
//        }
//    }
}