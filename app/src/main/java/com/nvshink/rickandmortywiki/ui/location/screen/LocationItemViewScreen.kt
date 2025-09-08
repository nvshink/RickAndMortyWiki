package com.nvshink.rickandmortywiki.ui.location.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.rickandmortywiki.R
import com.nvshink.rickandmortywiki.ui.character.state.CharacterSmallListUiState
import com.nvshink.rickandmortywiki.ui.generic.components.list.SmallListOfItems
import com.nvshink.rickandmortywiki.ui.utils.CharacterItemScreenRoute
import com.nvshink.rickandmortywiki.ui.utils.CharactersScreenRoute

@Composable
fun LocationItemViewScreen(
    location: LocationModel,
    residentsUiState: CharacterSmallListUiState,
    onNavigation: (Any) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            location.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            location.type,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            location.dimension,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        SmallListOfItems(
            isLoading = residentsUiState is CharacterSmallListUiState.LoadingState,
            errorMessage = if (residentsUiState is CharacterSmallListUiState.ErrorState) residentsUiState.error?.message ?: "" else null,
            listOfItems = if (residentsUiState is CharacterSmallListUiState.SuccessState) residentsUiState.characterList else emptyList(),
            listItem = @Composable fun (residentAny: Any) {
                (residentAny as? CharacterModel)?.let { resident ->
                    Box(modifier = Modifier.clickable {
                        onNavigation(CharacterItemScreenRoute(resident.id))
                    }){
                        Text(
                            text = resident.name,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        )
    }
}