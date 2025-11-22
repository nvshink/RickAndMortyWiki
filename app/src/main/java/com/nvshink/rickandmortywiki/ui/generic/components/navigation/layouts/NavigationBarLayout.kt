package com.nvshink.rickandmortywiki.ui.generic.components.navigation.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.nvshink.rickandmortywiki.ui.utils.Destinations

@Composable
fun NavigationBarLayout(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    onMenuItemSelected: (Any) -> Unit,
    content: @Composable () -> Unit
) {
    val topLevelRoutes = Destinations.getTopLevelRoutes()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar {
                topLevelRoutes.forEach { item ->
                    NavigationBarItem(
                        label = {
                            Text(text = item.name)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name
                            )
                        },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                        onClick = {
                            onMenuItemSelected(item.route)
                        },
                        enabled = item.isEnabled
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) { content() }
    }
}