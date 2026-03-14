package com.nvshink.rickandmortywiki.ui.generic.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nvshink.rickandmortywiki.ui.generic.components.box.LoadingBox

@Composable
fun ItemLoadingScreen (modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        LoadingBox()
    }
}