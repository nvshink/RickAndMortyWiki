package com.nvshink.rickandmortywiki.ui.utils

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat.getString
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.rickandmortywiki.R

fun CharacterGender.getName(context: Context): String {
    return when (this) {
        CharacterGender.MALE -> getString(context, R.string.character_gender_male)

        CharacterGender.FEMALE -> getString(context, R.string.character_gender_female)

        CharacterGender.GENDERLESS -> getString(context, R.string.character_gender_genderless)

        CharacterGender.UNKNOWN -> getString(context, R.string.character_gender_unknown)
    }
}

fun CharacterGender.getIcon(): ImageVector {
    return when (this) {
        CharacterGender.MALE -> Icons.Default.Male

        CharacterGender.FEMALE -> Icons.Default.Female

        CharacterGender.GENDERLESS -> Icons.Default.Transgender

        CharacterGender.UNKNOWN -> Icons.Default.QuestionMark
    }
}