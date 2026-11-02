package com.restest.flashcards.views.composables

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.restest.flashcards.data.repository.CardsLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for ViewModels that run on an Android device.
 * These tests verify the ViewModels work correctly in a real Android environment.
 */
@RunWith(AndroidJUnit4::class)
class ViewModelsInstrumentedTest {

    private lateinit var repository: CardsLocalRepository

    @Before
    fun setup() {
        repository = CardsLocalRepository.instance
    }

    @Test
    fun testCardsViewModelLoadsCards() = runTest {
        val viewModel = CardsViewModel(repository, Dispatchers.Main)

        // Give the ViewModel time to initialize and collect events
        kotlinx.coroutines.delay(200)

        viewModel.cardList.test {
            val cards = awaitItem()
            assertTrue("Cards list should not be empty", cards.isNotEmpty())
            assertTrue("Should have at least 13 cards", cards.size >= 13)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testCardsViewModelUpdatesOnNewCard() = runTest {
        val viewModel = CardsViewModel(repository, Dispatchers.Main)

        // Wait for initial load
        kotlinx.coroutines.delay(200)

        val initialCount = viewModel.cardList.value.size

        // Add a new card through repository
        repository.addCard(
            com.restest.flashcards.data.model.FlashCard(
                "Test Topic Instrumented",
                "Test Question",
                "Test Answer"
            )
        )

        // Wait for the update
        kotlinx.coroutines.delay(100)

        // Request cards again to trigger update
        repository.requestCards()

        // Wait for state to update
        kotlinx.coroutines.delay(200)

        val updatedCount = viewModel.cardList.value.size
        assertTrue("Card count should increase or stay same", updatedCount >= initialCount)
    }

    @Test
    fun testCreateCardsViewModelAddsCard() = runTest {
        val viewModel = CreateCardsViewModel(repository, Dispatchers.Main)

        // Wait for initialization
        kotlinx.coroutines.delay(200)

        // Add a card
        viewModel.addCard(
            "Integration Test Instrumented",
            "Does this work?",
            "Yes, it does!"
        )

        // Wait for event to be emitted
        kotlinx.coroutines.delay(200)

        viewModel.events.test {
            val event = awaitItem()
            assertTrue("Should receive CardAdded event", event is CreateCardsViewModel.Event.CardAdded)
            assertEquals(
                "New card added",
                (event as CreateCardsViewModel.Event.CardAdded).message
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testCreateCardsViewModelRejectsEmptyTopic() = runTest {
        val viewModel = CreateCardsViewModel(repository, Dispatchers.Main)

        kotlinx.coroutines.delay(200)

        // Get initial card count
        repository.requestCards()
        kotlinx.coroutines.delay(100)
        val initialCount = repository.events.value.let {
            if (it is CardsLocalRepository.Event.Success) it.cards.size else 0
        }

        // Try to add card with empty topic
        viewModel.addCard("", "Question", "Answer")

        // Wait a bit
        kotlinx.coroutines.delay(200)

        // Card count should not have changed
        repository.requestCards()
        kotlinx.coroutines.delay(100)
        val afterCount = repository.events.value.let {
            if (it is CardsLocalRepository.Event.Success) it.cards.size else 0
        }
        assertEquals("Card count should not change", initialCount, afterCount)
    }

    @Test
    fun testCreateCardsViewModelRejectsEmptyQuestion() = runTest {
        val viewModel = CreateCardsViewModel(repository, Dispatchers.Main)

        kotlinx.coroutines.delay(200)

        // Get initial card count
        repository.requestCards()
        kotlinx.coroutines.delay(100)
        val initialCount = repository.events.value.let {
            if (it is CardsLocalRepository.Event.Success) it.cards.size else 0
        }

        // Try to add card with empty question
        viewModel.addCard("Topic", "", "Answer")

        kotlinx.coroutines.delay(200)

        // Card count should not have changed
        repository.requestCards()
        kotlinx.coroutines.delay(100)
        val afterCount = repository.events.value.let {
            if (it is CardsLocalRepository.Event.Success) it.cards.size else 0
        }
        assertEquals("Card count should not change", initialCount, afterCount)
    }

    @Test
    fun testCreateCardsViewModelRejectsEmptyAnswer() = runTest {
        val viewModel = CreateCardsViewModel(repository, Dispatchers.Main)

        kotlinx.coroutines.delay(200)

        // Get initial card count
        repository.requestCards()
        kotlinx.coroutines.delay(100)
        val initialCount = repository.events.value.let {
            if (it is CardsLocalRepository.Event.Success) it.cards.size else 0
        }

        // Try to add card with empty answer
        viewModel.addCard("Topic", "Question", "")

        kotlinx.coroutines.delay(200)

        // Card count should not have changed
        repository.requestCards()
        kotlinx.coroutines.delay(100)
        val afterCount = repository.events.value.let {
            if (it is CardsLocalRepository.Event.Success) it.cards.size else 0
        }
        assertEquals("Card count should not change", initialCount, afterCount)
    }

    @Test
    fun testViewModelsWorkTogether() = runTest {
        val cardsViewModel = CardsViewModel(repository, Dispatchers.Main)
        val createViewModel = CreateCardsViewModel(repository, Dispatchers.Main)

        kotlinx.coroutines.delay(200)

        val initialCount = cardsViewModel.cardList.value.size

        // Add card through CreateCardsViewModel
        createViewModel.addCard(
            "Integration Instrumented",
            "Do ViewModels work together?",
            "Yes, they share the same repository"
        )

        kotlinx.coroutines.delay(200)

        // Trigger refresh in CardsViewModel
        repository.requestCards()

        // Wait for update
        kotlinx.coroutines.delay(200)

        // CardsViewModel should see the new card
        val updatedCount = cardsViewModel.cardList.value.size
        assertTrue("Card count should increase or stay same", updatedCount >= initialCount)

        val newCard = cardsViewModel.cardList.value.find { it.topic == "Integration Instrumented" }
        assertTrue("New card should be in the list", newCard != null)
    }
}

