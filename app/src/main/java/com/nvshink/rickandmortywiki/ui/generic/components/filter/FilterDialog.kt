package com.nvshink.rickandmortywiki.ui.generic.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.generic.components.topbar.ItemScreenTopBar
import com.nvshink.rickandmortywiki.ui.utils.ContentType

@Composable
fun FilterDialog(
    onDismissRequest: () -> Unit,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
    contentType: ContentType,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = when (contentType) {
                ContentType.LIST_ONLY -> false
                ContentType.LIST_AND_DETAIL -> true
            }
        )
    ) {
        Box(
            Modifier
                .clip(
                    when (contentType) {
                        ContentType.LIST_ONLY -> RoundedCornerShape(0.dp)
                        ContentType.LIST_AND_DETAIL -> MaterialTheme.shapes.extraLarge
                    }
                )
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
                    .width(200.dp)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ItemScreenTopBar(
                    contentType = contentType,
                    onBackButtonClicked = onDismissRequest
                )
                content()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    OutlinedButton(onClick = onReset) { Text(text = stringResource(R.string.button_reset)) }
                    Button(onClick = {
                        onConfirm()
                        onDismissRequest()
                    }) { Text(text = stringResource(R.string.button_confirm)) }
                }
            }
        }
    }
}