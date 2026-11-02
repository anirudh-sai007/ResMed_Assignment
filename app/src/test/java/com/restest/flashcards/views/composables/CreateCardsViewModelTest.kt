package com.restest.flashcards.views.composables

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.data.repository.CardsLocalRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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
class CreateCardsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: CardsLocalRepository
    private lateinit var viewModel: CreateCardsViewModel

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
    fun `viewModel initializes with Idle event`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            val initialEvent = awaitItem()
            assertTrue(initialEvent is CreateCardsViewModel.Event.Idle)
        }
    }

    @Test
    fun `addCard with valid inputs calls repository addCard`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val topic = "Kotlin"
        val question = "What is Kotlin?"
        val answer = "Kotlin is a programming language"

        viewModel.addCard(topic, question, answer)

        val cardSlot = slot<FlashCard>()
        verify { mockRepository.addCard(capture(cardSlot)) }

        assertEquals(topic, cardSlot.captured.topic)
        assertEquals(question, cardSlot.captured.question)
        assertEquals(answer, cardSlot.captured.answer)
    }

    @Test
    fun `addCard with empty topic does not call repository`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addCard("", "Question", "Answer")

        verify(exactly = 0) { mockRepository.addCard(any()) }
    }

    @Test
    fun `addCard with empty question does not call repository`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addCard("Topic", "", "Answer")

        verify(exactly = 0) { mockRepository.addCard(any()) }
    }

    @Test
    fun `addCard with empty answer does not call repository`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addCard("Topic", "Question", "")

        verify(exactly = 0) { mockRepository.addCard(any()) }
    }

    @Test
    fun `addCard with all empty fields does not call repository`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addCard("", "", "")

        verify(exactly = 0) { mockRepository.addCard(any()) }
    }

    @Test
    fun `events emits CardAdded when repository emits CardAdded`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            // Initial Idle event
            val initialEvent = awaitItem()
            assertTrue(initialEvent is CreateCardsViewModel.Event.Idle)

            // Emit CardAdded from repository
            val newCard = FlashCard("Test", "Test?", "Test answer")
            eventsFlow.value = CardsLocalRepository.Event.CardAdded(newCard)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should receive CardAdded event
            val cardAddedEvent = awaitItem()
            assertTrue(cardAddedEvent is CreateCardsViewModel.Event.CardAdded)
            assertEquals("New card added", (cardAddedEvent as CreateCardsViewModel.Event.CardAdded).message)
        }
    }

    @Test
    fun `events does not emit when repository emits Success`() = runTest {
        val eventsFlow = MutableStateFlow<CardsLocalRepository.Event>(CardsLocalRepository.Event.Idle)
        every { mockRepository.events } returns eventsFlow

        viewModel = CreateCardsViewModel(mockRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            awaitItem() // Initial Idle

            // Emit Success from repository
            eventsFlow.value = CardsLocalRepository.Event.Success(emptyList())
            testDispatcher.scheduler.advanceUntilIdle()

            // Should not emit new event
            expectNoEvents()
        }
    }
}

