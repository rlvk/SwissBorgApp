package com.example.swissborgapp

import app.cash.turbine.test
import com.example.swissborgapp.domain.ConnectivityObserver
import com.example.swissborgapp.domain.Status
import com.example.swissborgapp.domain.model.Ticker
import com.example.swissborgapp.domain.use_case.GetPublicTickersUseCase
import com.example.swissborgapp.presentation.MainViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val coroutineRule = TestDispatcherRule()

    private val connectivityObserver = mockk<ConnectivityObserver>()
    private val getPublicTickersUseCase = mockk<GetPublicTickersUseCase>()

    @Before
    fun setUp() {

        coEvery { getPublicTickersUseCase.invoke() } returns flowOf(Result.success(emptyList()))

        coEvery { connectivityObserver.observe() } returns flowOf(Status.Available)

        // setting up test dispatcher as main dispatcher for coroutines
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        // removing the test dispatcher
        Dispatchers.resetMain()
    }

    private fun initVM(): MainViewModel {
        return MainViewModel(
            getPublicTickersUseCase,
            connectivityObserver
        )
    }

    @Test
    fun `Given the vm is init, verify that default state is returned `() = runTest {
        // when
        val vm = initVM()
        vm.state.test {
            // then
            val item = expectMostRecentItem()

            assert(item == MainViewModel.State())
        }
    }

    @Test
    fun `Given public tickers api returns error, verify that it is being shown`() = runTest {
        // given
        val throwable = Throwable("Something went wrong")
        every { getPublicTickersUseCase.invoke() } returns flowOf(Result.failure(throwable))

        // when
        val vm = initVM()
        advanceUntilIdle()
        vm.state.test {
            // then
            val item = awaitItem()
            assert(item == MainViewModel.State(
                isLoading = false,
                error = throwable.message
            ))
        }
    }

    @Test
    fun `When that endpoint returns 2 tickers, verify that exact same ones are shown`() = runTest {
        // given
        val tickers = listOf(
            Ticker(
                symbol = "BTC",
                price = "65,000"
            ),
            Ticker(
                symbol = "ETH",
                price = "10,000"
            )
        )
        every { getPublicTickersUseCase.invoke() } returns flowOf(
            Result.success(
                tickers
            )
        )

        // when
        val vm = initVM()
        advanceUntilIdle()
        vm.state.test {
            // then
            val item = awaitItem()
            assert(item == MainViewModel.State(
                isLoading = false,
                tickers = tickers
            ))
        }
    }

    @Test
    fun `Given two tickers are returned from api, when search text matches one of them, verify that one ticker is returned`() = runTest {
        // given
        val tickers = listOf(
            Ticker(
                symbol = "BTC",
                price = "65,000"
            ),
            Ticker(
                symbol = "ETH",
                price = "10,000"
            )
        )
        every { getPublicTickersUseCase.invoke() } returns flowOf(
            Result.success(
                tickers
            )
        )

        // when
        val vm = initVM()
        advanceUntilIdle()
        vm.onSearchTextChanged("BTC")
        vm.state.test {
            // then
            val item = awaitItem()
            assert(item.publicTickers.first() == tickers.first())
        }
    }

    @Test
    fun `Given two tickers are returned from api, when search text matches one of them, verify that one ticker is returned v2`() = runTest {
        // given
        val tickers = listOf(
            Ticker(
                symbol = "BTC",
                price = "65,000"
            ),
            Ticker(
                symbol = "ETH",
                price = "10,000"
            )
        )
        every { getPublicTickersUseCase.invoke() } returns flowOf(
            Result.success(
                tickers
            )
        )

        // when
        val vm = initVM()
        advanceUntilIdle()
        vm.onSearchTextChanged("ETH")
        vm.state.test {
            // then
            val item = awaitItem()
            assert(item.publicTickers.first() == tickers[1])
        }
    }

    @Test
    fun `Given there is no connectivity, verify that no connection view is shown`() = runTest {
        // given
        every { connectivityObserver.observe() } returns flowOf(Status.Unavailable)

        // when
        val vm = initVM()
        advanceUntilIdle()
        vm.state.test {
            // then
            val item = awaitItem()
            assert(item == MainViewModel.State(
                isLoading = false,
                isConnected = false
            ))
        }
    }
}