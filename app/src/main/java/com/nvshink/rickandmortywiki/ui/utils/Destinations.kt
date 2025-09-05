package com.nvshink.rickandmortywiki.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

object Destinations {
    data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector, val isEnabled: Boolean)

    private val topLevelRoutes = listOf(
        TopLevelRoute("Characters", CharactersScreenRoute, Icons.Filled.Groups, true),
        TopLevelRoute("Locations", LocationsScreenRoute, Icons.Filled.LocationCity, true),
        TopLevelRoute("Episodes", EpisodesScreenRoute, Icons.Filled.Movie, true)
    )

    fun getTopLevelRoutes(): List<TopLevelRoute<out Any>> {
        return topLevelRoutes
    }

    fun getDefaultTopLevelRoute(): TopLevelRoute<out Any> {
        val defaultTopLevelRoute: TopLevelRoute<out Any> = topLevelRoutes[0]
        return defaultTopLevelRoute
    }
}

@Serializable
object CharactersScreenRoute

@Serializable
object LocationsScreenRoute

@Serializable
object EpisodesScreenRoute

@Serializable
object EmptyItemScreenRoute

@Serializable
data class CharacterItemScreenRoute(
    val id: Int
)

@Serializable
data class LocationItemScreenRoute(
    val id: Int
)
@Serializable
data class EpisodeItemScreenRoute(
    val id: Int
)