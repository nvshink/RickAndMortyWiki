package com.nvshink.rickandmortywiki.ui.episode.viewmodel

import app.cash.turbine.test
import com.nvshink.data.generic.local.datasource.DataSourceManager
import com.nvshink.domain.episode.model.EpisodeModel
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.episode.event.EpisodeDetailEvent
import com.nvshink.rickandmortywiki.ui.episode.state.EpisodeDetailUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.time.LocalDate
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodeDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: EpisodeRepository
    private lateinit var viewModel: EpisodeDetailViewModel

    private val mockEpisode = EpisodeModel(
        id = 1,
        name = "Pilot",
        airDate = LocalDate.now(),
        episode = "S01E01",
        characters = emptyList(),
        url = "https://rickandmortyapi.com/api/episode/1",
        created = ZonedDateTime.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = EpisodeDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is LoadingState`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is EpisodeDetailUiState.LoadingState)
        }
    }

    @Test
    fun `SetEpisode updates state to ViewState on success`() = runTest {
        val episodeId = 1
        coEvery { repository.getEpisodeByIdApi(episodeId) } returns flowOf(Resource.Success(mockEpisode))

        viewModel.uiState.test {
            viewModel.onEvent(EpisodeDetailEvent.SetEpisode(episodeId))
            
            assertTrue(awaitItem() is EpisodeDetailUiState.LoadingState)
            val state = awaitItem()
            assertTrue(state is EpisodeDetailUiState.ViewState)
            assertEquals(mockEpisode, (state as EpisodeDetailUiState.ViewState).episode)
        }
    }

    @Test
    fun `SetEpisode updates state to ErrorState on API failure and DB failure`() = runTest {
        val episodeId = 1
        val errorMessage = "Not found"
        coEvery { repository.getEpisodeByIdApi(episodeId) } returns flowOf(Resource.Error(errorMessage))
        coEvery { repository.getEpisodeByIdDB(episodeId) } returns flowOf(Resource.Error(errorMessage))

        viewModel.uiState.test {
            viewModel.onEvent(EpisodeDetailEvent.SetEpisode(episodeId))
            
            assertTrue(awaitItem() is EpisodeDetailUiState.LoadingState)
            val state = awaitItem()
            assertTrue(state is EpisodeDetailUiState.ErrorState)
            assertEquals(errorMessage, (state as EpisodeDetailUiState.ErrorState).message)
        }
    }
}
