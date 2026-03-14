package com.nvshink.rickandmortywiki.ui.location.viewmodel

import app.cash.turbine.test
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.repository.LocationRepository
import com.nvshink.rickandmortywiki.ui.location.event.LocationPageListEvent
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
class LocationPageListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: LocationRepository
    private lateinit var viewModel: LocationPageListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        every { repository.getLocationsStream(any()) } returns emptyFlow()
        
        viewModel = LocationPageListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty filter and default content type`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.filter.name)
            assertNull(state.filter.type)
            assertNull(state.filter.dimension)
            assertEquals("", state.searchBarText)
            assertFalse(state.isShowingFilter)
        }
    }

    @Test
    fun `SetFilter updates filter state`() = runTest {
        val newFilter = LocationFilterModel(
            name = "Earth",
            type = "Planet",
            dimension = "Dimension C-137"
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(LocationPageListEvent.SetUiStateFilter(newFilter))
            val state = awaitItem()
            assertEquals("Earth", state.filter.name)
            assertEquals("Planet", state.filter.type)
            assertEquals("Dimension C-137", state.filter.dimension)
        }
    }

    @Test
    fun `SetSearchBarText updates search bar text in UI state`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals("", initialState.searchBarText)

            viewModel.onEvent(LocationPageListEvent.SetSearchBarText("Mars"))

            val updatedState = awaitItem()
            assertEquals("Mars", updatedState.searchBarText)
        }
    }
}
