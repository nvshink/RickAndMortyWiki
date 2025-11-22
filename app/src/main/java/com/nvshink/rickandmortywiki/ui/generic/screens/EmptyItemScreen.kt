package com.nvshink.rickandmortywiki.ui.generic.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nvshink.rickandmortywiki.R

@Composable
fun EmptyItemScreen(
    modifier: Modifier = Modifier,
    title: String?,
    icon: ImageVector?,
    iconDescription: String,
    onRefresh: (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 20.dp)
            )
        }
        if (onRefresh != null) {
            Spacer(modifier = Modifier.size(32.dp))
            Button(onClick = onRefresh) {
                Text(text = stringResource(R.string.button_refresh))
            }
        }
    }
}