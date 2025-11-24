package com.nvshink.rickandmortywiki.ui.generic.components.box

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorBox (errorMessage: String, onRetryClick: () -> Unit, onOfflineClick: ((Boolean) -> Unit)? = null) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = errorMessage, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Button(onClick = onRetryClick) {
                    Text(stringResource(R.string.button_retry))
                }
                if (onOfflineClick != null) {
                    TextButton(onClick = { onOfflineClick(true) }) {
                        Text(stringResource(R.string.button_offline_turn_on))
                    }
                }
            }
        }
    }
}