package com.nvshink.rickandmortywiki.ui.generic.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nvshink.rickandmortywiki.ui.generic.components.box.LoadingBox

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItemLoadingScreen () {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingBox()
    }
}