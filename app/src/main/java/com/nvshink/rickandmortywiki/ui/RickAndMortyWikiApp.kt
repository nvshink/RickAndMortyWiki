package com.nvshink.rickandmortywiki.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nvshink.rickandmortywiki.ui.character.screen.CharacterScreen
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterPageListViewModel
import com.nvshink.rickandmortywiki.ui.episode.screen.EpisodeScreen
import com.nvshink.rickandmortywiki.ui.episode.viewmodel.EpisodeViewModel
import com.nvshink.rickandmortywiki.ui.generic.components.navigation.NavigationBarLayout
import com.nvshink.rickandmortywiki.ui.generic.components.navigation.NavigationRailLayout
import com.nvshink.rickandmortywiki.ui.generic.components.navigation.PermanentNavigationDrawerLayout
import com.nvshink.rickandmortywiki.ui.location.screen.LocationScreen
import com.nvshink.rickandmortywiki.ui.location.viewmodel.LocationPageListViewModel
import com.nvshink.rickandmortywiki.ui.utils.CharactersScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.Destinations
import com.nvshink.rickandmortywiki.ui.utils.EpisodesScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.LocationsScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.NavigationType

@Composable
fun RickAndMortyWikiApp(
    windowSize: WindowWidthSizeClass
) {
    val contentType: ContentType
    val navigationType: NavigationType
    var screensShape = RoundedCornerShape(0.dp)
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            contentType = ContentType.LIST_ONLY
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }

        WindowWidthSizeClass.Medium -> {
            contentType = ContentType.LIST_ONLY
            navigationType = NavigationType.NAVIGATION_RAIL
            screensShape = MaterialTheme.shapes.extraLarge.copy(
                topEnd = CornerSize(
                    0.dp
                ), bottomEnd = CornerSize(0.dp)
            ) as RoundedCornerShape
        }

        WindowWidthSizeClass.Expanded -> {
            contentType = ContentType.LIST_AND_DETAIL
            navigationType = NavigationType.NAVIGATION_RAIL
            screensShape = MaterialTheme.shapes.extraLarge.copy(
                topEnd = CornerSize(
                    0.dp
                ), bottomEnd = CornerSize(0.dp)
            ) as RoundedCornerShape
        }

        else -> {
            contentType = ContentType.LIST_ONLY
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }
    }

    val navController = rememberNavController()
    val navHost = remember {
        movableContentOf<PaddingValues> { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destinations.getDefaultTopLevelRoute().route
            ) {
                composable<CharactersScreenRoute> {
                    val characterPageListViewModel: CharacterPageListViewModel = hiltViewModel()
                    val characterListUiState = characterPageListViewModel.uiState.collectAsState().value
                    CharacterScreen(
                        modifier = Modifier
                            .clip(
                                screensShape
                            )
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        characterPageListUiState = characterListUiState,
                        onCharacterListEvent = characterPageListViewModel::onEvent,
                        characterScreenModifier = Modifier
                            .clip(
                                screensShape
                            )
                            .background(MaterialTheme.colorScheme.surface),
                        contentType = contentType,
                        innerPadding = innerPadding
                    )
                }
                composable<LocationsScreenRoute> {
                    val locationPageListViewModel: LocationPageListViewModel = hiltViewModel()
                    val locationListUiState = locationPageListViewModel.uiStateSmallList.collectAsState().value
                    LocationScreen(
//                        modifier = Modifier
//                            .clip(
//                                screensShape
//                            )
//                            .background(
//                                MaterialTheme.colorScheme.surfaceContainer
//                            ),
//                        exerciseListUiState = exerciseListUiState,
//                        onExerciseListEvent = exerciseListViewModel::onListEvent,
//                        exerciseScreenModifier = Modifier
//                            .clip(
//                                screensShape
//                            )
//                            .background(MaterialTheme.colorScheme.surface),
//                        contentType = contentType,
//                        innerPadding = innerPadding
                    )
                }
                composable<EpisodesScreenRoute> {
                    val episodeViewModel: EpisodeViewModel = hiltViewModel()
                    val episodeUiState = episodeViewModel.uiStateSmallList.collectAsState().value
                    EpisodeScreen(
//                        modifier = Modifier.padding(innerPadding),
//                        contentType = contentType,
//                        onTrainingPlanItemScreenBackPressed = {}
                    )
                }

            }
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationBarLayout = @Composable {
        NavigationBarLayout(
            modifier = Modifier,
            currentDestination = currentDestination,
            onMenuItemSelected = {
                navController.navigate(route = it) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) { innerPadding ->
            navHost(innerPadding)
        }
    }

    val navigationRailLayout = @Composable {
        NavigationRailLayout(
            modifier = Modifier,
            currentDestination = currentDestination,
            onMenuItemSelected = {
                navController.navigate(it) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) { innerPadding ->
            navHost(innerPadding)
        }
    }

    val permanentNavigationDrawerLayout = @Composable {
        PermanentNavigationDrawerLayout(
            modifier = Modifier,
            currentDestination = currentDestination,
            onMenuItemSelected = {
                navController.navigate(it) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) { innerPadding ->
            navHost(innerPadding)
        }
    }

    when (navigationType) {
        NavigationType.BOTTOM_NAVIGATION -> {
            navigationBarLayout()
        }

        NavigationType.NAVIGATION_RAIL -> {
            navigationRailLayout()
        }

        NavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            permanentNavigationDrawerLayout()
        }
    }
}