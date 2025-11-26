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

object ScreenTransitions {
    fun enterTransition(): EnterTransition = fadeIn(tween(300))

    fun exitTransition(): ExitTransition = fadeOut(tween(300))


    fun popEnterTransition(): EnterTransition = slideInHorizontally(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        initialOffsetX = { fullWidth -> -fullWidth / 4 }
    ) + fadeIn(tween(durationMillis = 200)) + scaleIn(
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        0.7f
    )


    fun popExitTransition(): ExitTransition = slideOutHorizontally(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        targetOffsetX = { fullWidth -> fullWidth }
    ) + scaleOut(spring(dampingRatio = Spring.DampingRatioMediumBouncy), 0.9f)
}