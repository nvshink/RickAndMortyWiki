package com.nvshink.rickandmortywiki.ui.character.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.rickandmortywiki.ui.utils.getIcon
import com.nvshink.rickandmortywiki.ui.utils.getName

@Composable
fun CharacterPageListCardContent(
    character: CharacterModel
) {
    val context = LocalContext.current

    val imageRequest = remember(character.id) {
        ImageRequest.Builder(context)
            .data(character.image)
            .crossfade(true)
            .size(400)
            .precision(Precision.INEXACT)
            .build()
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = character.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(
                        alpha = 0.6f
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 6.dp)
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Row вместо FlowRow для экономии ресурсов на Layout phase
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                        .fillMaxWidth()
                ) {
                    CardContentTag(
                        modifier = Modifier.weight(1f, fill = false),
                        text = character.species
                    )
                    CardContentTag(
                        modifier = Modifier.weight(1f, fill = false),
                        text = character.gender.getName(context = context),
                        additionalContent = { size ->
                            Icon(
                                imageVector = character.gender.getIcon(),
                                contentDescription = null,
                                modifier = Modifier.size(size)
                            )
                        }
                    )
                    CardContentTag(
                        modifier = Modifier.weight(1f, fill = false),
                        text = character.status.getName(context = context),
                        additionalContent = { size ->
                            Icon(
                                imageVector = character.status.getIcon(),
                                contentDescription = null,
                                modifier = Modifier.size(size)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardContentTag(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    text: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    additionalContent: @Composable ((size: Dp) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .background(color = backgroundColor, shape = CircleShape)
            .padding(6.dp)
            .height(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text != null) {
            Text(
                text = text, 
                color = textColor, 
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (additionalContent != null) {
            Spacer(Modifier.size(4.dp))
            additionalContent(16.dp)
        }
    }
}
