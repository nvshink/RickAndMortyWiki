package com.nvshink.rickandmortywiki.ui.generic.components.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.screen.CharacterItemScreen
import com.nvshink.rickandmortywiki.ui.character.viewmodel.CharacterDetailViewModel
import com.nvshink.rickandmortywiki.ui.generic.screens.EmptyItemScreen
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.screen.LocationItemScreen
import com.nvshink.rickandmortywiki.ui.location.viewmodel.LocationDetailViewModel
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.ContentType
import com.nvshink.rickandmortywiki.ui.utils.EmptyItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.LocationItemScreenRoute

@Composable
fun DynamicNavigation(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    itemModifier: Modifier = Modifier,
    startDestination: Any
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            enterTransition()
        },
        exitTransition = {
            exitTransition()
        },
        popEnterTransition = {
            popEnterTransition()
        },
        popExitTransition = {
            popExitTransition()
        }
    ) {
        composable<CharacterItemScreenRoute> { nav ->
            val characterDetailViewModel: CharacterDetailViewModel = hiltViewModel()
            val characterDetailUiState =
                characterDetailViewModel.uiState.collectAsState().value
            val onCharacterDetailEvent = characterDetailViewModel::onEvent
            val args = nav.toRoute<CharacterItemScreenRoute>()
            onCharacterDetailEvent(CharacterDetailEvent.SetCharacter(args.id))
            CharacterItemScreen(
                modifier = itemModifier,
                detailUiState = characterDetailUiState,
                onRefreshClick = {
                    onCharacterDetailEvent(CharacterDetailEvent.Refresh)
                },
                onBackClick = {
                    if (!navController.navigateUp()) {
                        onBack()
                    }
                },
                navController = navController,
                contentType = ContentType.LIST_ONLY
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
                modifier = itemModifier,
                detailUiState = locationDetailUiState,
                onBackPressed = {
                    navController.navigateUp()
                },
                navController = navController,
                contentType = ContentType.LIST_ONLY
            )
        }
        composable<EmptyItemScreenRoute> { nav ->
            EmptyItemScreen(
                title = stringResource(R.string.empty_screen_title_character),
                icon = Icons.Default.Person,
                iconDescription = stringResource(R.string.empty_screen_icon_description_character)
            )
        }
    }
}

fun enterTransition(): EnterTransition = fadeIn(tween(300))

fun exitTransition(): ExitTransition = fadeOut(tween(300))


fun popEnterTransition(): EnterTransition = slideInHorizontally(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    initialOffsetX = { fullWidth -> -fullWidth / 4 }
) + fadeIn(tween(durationMillis = 200)) + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), 0.7f)


fun popExitTransition(): ExitTransition = slideOutHorizontally(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    targetOffsetX =  { fullWidth -> fullWidth}
) + scaleOut(spring(dampingRatio = Spring.DampingRatioMediumBouncy), 0.9f)

