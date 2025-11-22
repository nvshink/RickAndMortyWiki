package com.nvshink.rickandmortywiki.ui.character.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterLocationModel
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.ui.utils.getIcon
import com.nvshink.rickandmortywiki.ui.utils.getName
import java.time.ZonedDateTime

@Composable
fun CharacterPageListCardContent(
    character: CharacterModel
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .size(150.dp),
        Alignment.Center
    )
    {
        AsyncImage(
            model = character.image,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceBright),
            error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(2.dp),
            contentScale = ContentScale.FillWidth
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
                Modifier
                    .fillMaxSize(),
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
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    itemVerticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    CardContentTag(
                        text = character.species
                    )
                    CardContentTag(
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
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    text: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    additionalContent: @Composable ((size: Dp) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(color = backgroundColor, shape = CircleShape)
            .padding(6.dp)
            .height(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text != null) {
            Text(text = text, color = textColor, style = MaterialTheme.typography.bodySmall)
        }
        if (additionalContent != null) {
            Spacer(Modifier.size(4.dp))
            additionalContent(16.dp)
        }
    }
}

@Preview
@Composable
fun CharacterPageListCardContentPreview() {
    CharacterPageListCardContent(character = CharacterModel(
        id = 0,
        name = "Test name",
        status = CharacterStatus.UNKNOWN,
        species = "Human",
        type = "Type",
        gender = CharacterGender.UNKNOWN,
        origin = CharacterLocationModel(
            id = 0,
            name = "",
            url = "https://rickandmortyapi.com/api/location/1"
        ),
        location = CharacterLocationModel(
            id = 0,
            name = "",
            url = "https://rickandmortyapi.com/api/location/1"
        ),
        image = "https://rickandmortyapi.com/api/character/avatar/5.jpeg",
        episode = emptyList(),
        url = "",
        created = ZonedDateTime.now()
    ))
}