package com.nvshink.rickandmortywiki.ui.character.viewmodel

import app.cash.turbine.test
import com.nvshink.domain.character.model.CharacterFilterModel
import com.nvshink.domain.character.model.CharacterGender
import com.nvshink.domain.character.model.CharacterStatus
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.rickandmortywiki.ui.character.event.CharacterPageListEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterPageListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CharacterRepository
    private lateinit var viewModel: CharacterPageListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        every { repository.getCharactersStream(any()) } returns emptyFlow()

        viewModel = CharacterPageListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty filter and no search text`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.filter.name)
            assertNull(state.filter.status)
            assertNull(state.filter.species)
            assertNull(state.filter.gender)
            assertEquals("", state.searchBarText)
            assertFalse(state.isShowingFilter)
        }
    }

    @Test
    fun `SetFilter updates filter state`() = runTest {
        val newFilter = CharacterFilterModel(
            name = "Rick",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = CharacterGender.MALE
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(CharacterPageListEvent.SetUiStateFilter(newFilter))
            val state = awaitItem()
            assertEquals("Rick", state.filter.name)
            assertEquals(CharacterStatus.ALIVE, state.filter.status)
            assertEquals("Human", state.filter.species)
            assertEquals(CharacterGender.MALE, state.filter.gender)
        }
    }

    @Test
    fun `SetSearchBarText updates search bar text in UI state`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals("", initialState.searchBarText)

            viewModel.onEvent(CharacterPageListEvent.SetSearchBarText("Morty"))

            val updatedState = awaitItem()

            assertEquals("Morty", updatedState.searchBarText)
        }
    }

    @Test
    fun `ShowFilterDialog sets isShowingFilter to true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(CharacterPageListEvent.ShowFilterDialog)
            val state = awaitItem()
            assertTrue(state.isShowingFilter)
        }
    }

    @Test
    fun `HideFilterDialog sets isShowingFilter to false`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(CharacterPageListEvent.ShowFilterDialog)
            awaitItem()
            viewModel.onEvent(CharacterPageListEvent.HideFilterDialog)
            val state = awaitItem()
            assertFalse(state.isShowingFilter)
        }
    }

    @Test
    fun `ClearFilterUi resets filter to empty values`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(
                CharacterPageListEvent.SetUiStateFilter(
                    CharacterFilterModel(
                        name = "Rick",
                        status = CharacterStatus.DEAD,
                        gender = CharacterGender.FEMALE
                    )
                )
            )
            awaitItem()
            viewModel.onEvent(CharacterPageListEvent.ClearFilterUi)
            val state = awaitItem()
            assertNull(state.filter.name)
            assertNull(state.filter.status)
            assertNull(state.filter.species)
            assertNull(state.filter.gender)
        }
    }
}
