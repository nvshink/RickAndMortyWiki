package com.nvshink.rickandmortywiki.ui.generic.components.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun ErrorBox (errorMessage: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        Row { Text(errorMessage)
            TextButton(onClick = onClick) {
                Text(stringResource(R.string.button_retry))
            }
        }
    }
}