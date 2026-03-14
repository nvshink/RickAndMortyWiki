package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import app.cash.turbine.test
import com.nvshink.domain.episode.model.EpisodeFilterModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodePageListEvent
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
class EpisodePageListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: EpisodeRepository
    private lateinit var viewModel: EpisodePageListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.getEpisodesStream(any()) } returns emptyFlow()
        
        viewModel = EpisodePageListViewModel(repository)
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
            assertNull(state.filter.episode)
            assertEquals("", state.searchBarText)
            assertFalse(state.isShowingFilter)
        }
    }

    @Test
    fun `SetFilter updates filter state`() = runTest {
        val newFilter = EpisodeFilterModel(
            name = "Pilot",
            episode = "S01E01"
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.SetUiStateFilter(newFilter))
            val state = awaitItem()
            assertEquals("Pilot", state.filter.name)
            assertEquals("S01E01", state.filter.episode)
        }
    }

    @Test
    fun `SetSearchBarText updates search bar text in UI state`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals("", initialState.searchBarText)

            viewModel.onEvent(EpisodePageListEvent.SetSearchBarText("Rick"))

            val updatedState = awaitItem()
            assertEquals("Rick", updatedState.searchBarText)
        }
    }

    @Test
    fun `ShowFilterDialog sets isShowingFilter to true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.ShowFilterDialog)
            val state = awaitItem()
            assertTrue(state.isShowingFilter)
        }
    }

    @Test
    fun `HideFilterDialog sets isShowingFilter to false`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.ShowFilterDialog)
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.HideFilterDialog)
            val state = awaitItem()
            assertFalse(state.isShowingFilter)
        }
    }

    @Test
    fun `ClearFilterUi resets filter to empty values`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.SetUiStateFilter(
                EpisodeFilterModel(
                    name = "Pilot",
                    episode = "S01E01"
                )
            ))
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.ClearFilterUi)
            val state = awaitItem()
            assertNull(state.filter.name)
            assertNull(state.filter.episode)
        }
    }

    @Test
    fun `SetUiStateFilter updates UI state filter`() = runTest {
        val filter = EpisodeFilterModel(
            name = "Test",
            episode = "S02E05"
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(EpisodePageListEvent.SetUiStateFilter(filter))
            val state = awaitItem()
            assertEquals("Test", state.filter.name)
            assertEquals("S02E05", state.filter.episode)
        }
    }
}
