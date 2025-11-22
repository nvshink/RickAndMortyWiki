package com.nvshink.rickandmortywiki.ui.generic.components.navigation.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.nvshink.rickandmortywiki.ui.utils.Destinations

@Composable
fun PermanentNavigationDrawerLayout(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    onMenuItemSelected: (Any) -> Unit,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    val topLevelRoutes = Destinations.getTopLevelRoutes()
    Scaffold { innerPadding ->
        Row(
            modifier = modifier
                .fillMaxSize()
        ) {
            PermanentNavigationDrawer(
                modifier = Modifier.Companion.width(80.dp),
                drawerContent = {
                    topLevelRoutes.forEach { item ->
                        NavigationDrawerItem(
                            modifier = Modifier.Companion.padding(horizontal = 12.dp),
                            label = {
                                Text(text = item.name, textAlign = TextAlign.Companion.Center)
                            },
                            icon = {
                                Icon(imageVector = item.icon, contentDescription = item.name)
                            },
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                            onClick = {
                                if (item.isEnabled) onMenuItemSelected(item.route)
                            }
                        )
                    }
                }
            )
            { content(innerPadding) }
        }
    }
}