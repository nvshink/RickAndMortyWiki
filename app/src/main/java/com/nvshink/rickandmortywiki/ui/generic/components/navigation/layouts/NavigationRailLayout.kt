package com.nvshink.rickandmortywiki.ui.generic.components.navigation.layouts

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
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
fun NavigationRailLayout(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    onMenuItemSelected: (Any) -> Unit,
    content: @Composable () -> Unit
) {
    val topLevelRoutes = Destinations.getTopLevelRoutes()
    Scaffold { innerPadding ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavigationRail(
                windowInsets = WindowInsets()
            ) {
                topLevelRoutes.forEach { item ->
                    NavigationRailItem(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        label = {
                            Text(text = item.name, textAlign = TextAlign.Center)
                        },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.name)
                        },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                        onClick = {
                            onMenuItemSelected(item.route)
                        },
                        enabled = item.isEnabled
                    )
                }
            }
            content()
        }
    }
}

