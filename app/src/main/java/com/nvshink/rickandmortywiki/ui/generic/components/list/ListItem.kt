package com.nvshink.rickandmortywiki.ui.generic.components.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    onCardClick: (() -> Unit)?,
    content: @Composable (() -> Unit)
) {
    if (onCardClick != null) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            onClick = onCardClick
        ) {
            content()
        }
    } else {
        content()
    }
}
