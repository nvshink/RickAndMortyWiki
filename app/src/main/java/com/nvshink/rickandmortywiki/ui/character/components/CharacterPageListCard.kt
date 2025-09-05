package com.nvshink.rickandmortywiki.ui.character.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.R

@Composable
fun CharacterPageListCard(
    character: CharacterModel
) {
    val genderText: Int
    val genderColor: Color
    val genderIcon: ImageVector
    when (character.gender) {
        CharacterGender.MALE -> {
            genderText = R.string.character_gender_male
            genderColor = Color(0xFF67C3E7)
            genderIcon = Icons.Default.Male
        }


        CharacterGender.FEMALE -> {
            genderText = R.string.character_gender_female
            genderColor = Color(0xFFE54F7F)
            genderIcon = Icons.Default.Female
        }

        CharacterGender.GENDERLESS -> {
            genderText = R.string.character_gender_genderless
            genderColor = Color(0xFF36DC65)
            genderIcon = Icons.Default.Circle
        }

        CharacterGender.UNKNOWN -> {
            genderText = R.string.character_gender_unknown
            genderColor = Color(0xFF9D9D9D)
            genderIcon = Icons.Default.QuestionMark
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .size(150.dp),
        Alignment.Center
    )
    {
        AsyncImage(
            character.image,
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
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier

                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    itemVerticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = character.species,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White
                        ),
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.Black)
                            .padding(6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(genderColor)
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(genderText),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Icon(
                            imageVector = genderIcon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.Black)
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = character.status.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White
                            ),
                        )
                        Box(
                            Modifier
                                .clip(
                                    CircleShape
                                )
                                .size(10.dp)
                                .background(
                                    when (character.status) {
                                        CharacterStatus.ALIVE -> Color.Green
                                        CharacterStatus.DEAD -> Color.Red
                                        CharacterStatus.UNKNOWN -> Color.Yellow
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}