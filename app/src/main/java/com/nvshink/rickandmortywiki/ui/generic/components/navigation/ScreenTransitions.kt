package com.nvshink.rickandmortywiki.ui.generic.components.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset

object ScreenTransitions {
    private fun <T> adaptiveSpringSpec(visibilityThreshold: T? = null) = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = visibilityThreshold
    )

    fun enterTransition(): EnterTransition = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = adaptiveSpringSpec(IntOffset.VisibilityThreshold)
    ) + fadeIn(adaptiveSpringSpec())

    fun exitTransition(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = adaptiveSpringSpec(IntOffset.VisibilityThreshold)
    ) + fadeOut(adaptiveSpringSpec())

    fun popEnterTransition(): EnterTransition = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = adaptiveSpringSpec(IntOffset.VisibilityThreshold)
    ) + fadeIn(adaptiveSpringSpec())

    fun popExitTransition(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = adaptiveSpringSpec(IntOffset.VisibilityThreshold)
    ) + fadeOut(adaptiveSpringSpec())
}
