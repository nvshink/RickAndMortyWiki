package com.nvshink.rickandmortywiki.ui.generic.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyItemScreen(
    title: String?,
    icon: ImageVector?,
    iconDescription: String,
    colors: EmptyItemScreenColors = EmptyItemScreenColors(
        iconTintColor = MaterialTheme.colorScheme.outline,
        textColor = MaterialTheme.colorScheme.outline
    )
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                modifier = Modifier.size(64.dp),
                tint = colors.iconTintColor
            )
        }
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = colors.textColor,
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 20.dp)
            )
        }
    }
}

class EmptyItemScreenColors (
    val iconTintColor: Color,
    val textColor: Color,
) {
    /**
     * Returns a copy of this ButtonColors, optionally overriding some of the values. This uses the
     * Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        containerColor: Color = this.iconTintColor,
        contentColor: Color = this.textColor,
    ) =
        EmptyItemScreenColors(
            containerColor.takeOrElse { this.iconTintColor },
            contentColor.takeOrElse { this.textColor },
        )
}