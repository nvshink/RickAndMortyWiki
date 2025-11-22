package com.nvshink.rickandmortywiki.ui.utils

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat.getString
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.rickandmortywiki.R

fun CharacterStatus.getName(context: Context): String {
    return when (this) {
        CharacterStatus.ALIVE -> getString(context, R.string.character_status_alive)
        CharacterStatus.DEAD -> getString(context, R.string.character_status_dead)
        CharacterStatus.UNKNOWN -> getString(context, R.string.character_status_unknown)
    }
}

fun CharacterStatus.getIcon(): ImageVector {
    return when (this) {
        CharacterStatus.ALIVE -> Icons.Default.Favorite
        CharacterStatus.DEAD -> Icons.Default.HeartBroken
        CharacterStatus.UNKNOWN -> Icons.Default.QuestionMark
    }
}