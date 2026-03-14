package com.nvshink.rickandmortywiki.ui.character.viewmodel

import app.cash.turbine.test
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.character.event.CharacterDetailEvent
import com.nvshink.rickandmortywiki.ui.character.state.CharacterDetailUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CharacterRepository
    private lateinit var dataSourceManager: DataSourceManager
    private lateinit var viewModel: CharacterDetailViewModel

    private val mockCharacter = CharacterModel(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        type = "",
        gender = CharacterGender.MALE,
        origin = mockk(relaxed = true),
        location = mockk(relaxed = true),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf("https://rickandmortyapi.com/api/episode/1"),
        url = "https://rickandmortyapi.com/api/character/1",
        created = ZonedDateTime.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        dataSourceManager = mockk(relaxed = true)
        
        coEvery { repository.getCharacterByIdApi(any()) } returns flowOf(Resource.Loading())
        
        viewModel = CharacterDetailViewModel(repository, dataSourceManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is LoadingState`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is CharacterDetailUiState.LoadingState)
        }
    }

    @Test
    fun `SetCharacter with valid id loads character from API`() = runTest {
        val characterId = 1
        coEvery { repository.getCharacterByIdApi(characterId) } returns flowOf(Resource.Success(mockCharacter))

        viewModel.onEvent(CharacterDetailEvent.SetCharacter(characterId))

        viewModel.uiState.test {
            awaitItem() // Skip initial loading
            val state = awaitItem()
            assertTrue(state is CharacterDetailUiState.ViewState)
            assertEquals(mockCharacter, (state as CharacterDetailUiState.ViewState).character)
        }
    }
}
