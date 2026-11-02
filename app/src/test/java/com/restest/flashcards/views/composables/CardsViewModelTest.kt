package com.restest.flashcards.views.composables

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.data.repository.CardsLocalRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: CardsLocalRepository
    private lateinit var viewModel: CardsViewModel

    private val testCards = listOf(
        FlashCard("Android", "What is Android?", "Android is an OS"),
        FlashCard("Kotlin", "What is Kotlin?", "Kotlin is a language")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel initializes and requests cards on creation`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockRepository.requestCards() }
    }

    @Test
    fun `cardList is empty initially`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CardsViewModel(mockRepository, testDispatcher)
        
        viewModel.cardList.test {
            val initialList = awaitItem()
            assertTrue(initialList.isEmpty())
        }
    }

    @Test
    fun `cardList updates when Success event is received`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cardList.test {
            // Initial empty list
            val initialList = awaitItem()
            assertTrue(initialList.isEmpty())

            // Emit Success event
            eventsFlow.value = CardsLocalRepository.Event.Success(testCards)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should receive updated list
            val updatedList = awaitItem()
            assertEquals(2, updatedList.size)
            assertEquals("Android", updatedList[0].topic)
            assertEquals("Kotlin", updatedList[1].topic)
        }
    }

    @Test
    fun `cardList does not update on CardAdded event`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cardList.test {
            val initialList = awaitItem()
            assertTrue(initialList.isEmpty())

            // Emit CardAdded event
            val newCard = FlashCard("Test", "Test?", "Test answer")
            eventsFlow.value = CardsLocalRepository.Event.CardAdded(newCard)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should not emit new value (list stays empty)
            expectNoEvents()
        }
    }

    @Test
    fun `cardList does not update on Error event`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cardList.test {
            val initialList = awaitItem()
            assertTrue(initialList.isEmpty())

            // Emit Error event
            eventsFlow.value = CardsLocalRepository.Event.Error("Test error")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should not emit new value
            expectNoEvents()
        }
    }

    @Test
    fun `multiple Success events update cardList correctly`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cardList.test {
            awaitItem() // Initial empty list

            // First Success event
            eventsFlow.value = CardsLocalRepository.Event.Success(testCards)
            testDispatcher.scheduler.advanceUntilIdle()
            val firstUpdate = awaitItem()
            assertEquals(2, firstUpdate.size)

            // Second Success event with more cards
            val moreCards = testCards + FlashCard("Swift", "What is Swift?", "Swift is a language")
            eventsFlow.value = CardsLocalRepository.Event.Success(moreCards)
            testDispatcher.scheduler.advanceUntilIdle()
            val secondUpdate = awaitItem()
            assertEquals(3, secondUpdate.size)
        }
    }
}

