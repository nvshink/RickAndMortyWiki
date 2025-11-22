package com.nvshink.rickandmortywiki.ui.location.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nvshink.domain.location.model.LocationModel

@Composable
fun LocationPageListCardContent(location: LocationModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(location.name, style = MaterialTheme.typography.titleMedium)
        Text(location.dimension, style = MaterialTheme.typography.titleSmall)
    }
}