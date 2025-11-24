package com.nvshink.rickandmortywiki.ui.location.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.R

@Composable
fun LocationPageListCardContent(location: LocationModel) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        Text(location.name, style = MaterialTheme.typography.titleMedium)
        Text("${stringResource(R.string.location_dimension)}: ${location.dimension}", style = MaterialTheme.typography.titleSmall)
    }
}