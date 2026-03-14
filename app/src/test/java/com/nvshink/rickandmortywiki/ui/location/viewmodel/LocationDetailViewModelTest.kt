package com.nvshink.rickandmortywiki.ui.location.viewmodel

import app.cash.turbine.test
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.domain.resource.Resource
import com.nvshink.rickandmortywiki.ui.location.event.LocationDetailEvent
import com.nvshink.rickandmortywiki.ui.location.state.LocationDetailUiState
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
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class LocationDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: LocationRepository
    private lateinit var viewModel: LocationDetailViewModel

    private val mockLocation = LocationModel(
        id = 1,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137",
        residents = emptyList(),
        url = "https://rickandmortyapi.com/api/location/1",
        created = ZonedDateTime.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = LocationDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is LoadingState`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is LocationDetailUiState.LoadingState)
        }
    }

    @Test
    fun `SetLocation with valid id loads location from API`() = runTest {
        val locationId = 1
        coEvery { repository.getLocationByIdApi(locationId) } returns flowOf(Resource.Success(mockLocation))

        viewModel.onEvent(LocationDetailEvent.SetLocation(locationId))

        viewModel.uiState.test {
            assertTrue(awaitItem() is LocationDetailUiState.LoadingState)
            val state = awaitItem()
            assertTrue(state is LocationDetailUiState.ViewState)
            assertEquals(mockLocation, (state as LocationDetailUiState.ViewState).location)
        }
    }

    @Test
    fun `SetLocation falls back to DB when API fails`() = runTest {
        val locationId = 1
        coEvery { repository.getLocationByIdApi(locationId) } returns flowOf(Resource.Error("API Error"))
        coEvery { repository.getLocationByIdDB(locationId) } returns flowOf(Resource.Success(mockLocation))

        viewModel.onEvent(LocationDetailEvent.SetLocation(locationId))

        viewModel.uiState.test {
            assertTrue(awaitItem() is LocationDetailUiState.LoadingState)
            val state = awaitItem()
            assertTrue(state is LocationDetailUiState.ViewState)
            assertEquals(mockLocation, (state as LocationDetailUiState.ViewState).location)
        }
    }
}
