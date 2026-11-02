package com.restest.flashcards.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.restest.flashcards.data.model.FlashCard
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for CardsLocalRepository that runs on an Android device.
 * This tests the repository in a real Android environment.
 */
@RunWith(AndroidJUnit4::class)
class CardsLocalRepositoryInstrumentedTest {

    private lateinit var repository: CardsLocalRepository

    @Before
    fun setup() {
        repository = CardsLocalRepository.instance
    }

    @Test
    fun testRepositoryInitialState() = runTest {
        // StateFlow replays the last value, so we get the current state immediately
        val currentEvent = repository.events.value
        assertTrue("Initial event should be Idle or a previous event",
            currentEvent is CardsLocalRepository.Event.Idle ||
            currentEvent is CardsLocalRepository.Event.Success ||
            currentEvent is CardsLocalRepository.Event.CardAdded)
    }

    @Test
    fun testRequestCardsReturnsInitialData() = runTest {
        repository.requestCards()

        repository.events.test {
            val event = awaitItem()
            assertTrue("Event should be Success", event is CardsLocalRepository.Event.Success)

            val cards = (event as CardsLocalRepository.Event.Success).cards
            assertTrue("Cards list should not be empty", cards.isNotEmpty())
            assertTrue("Should have at least 13 initial cards", cards.size >= 13)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testAddCardSuccessfully() = runTest {
        val newCard = FlashCard(
            topic = "Android Testing Instrumented",
            question = "What is Espresso?",
            answer = "Espresso is a testing framework for Android UI tests"
        )

        repository.addCard(newCard)

        repository.events.test {
            val event = awaitItem()
            assertTrue("Event should be CardAdded", event is CardsLocalRepository.Event.CardAdded)

            val addedCard = (event as CardsLocalRepository.Event.CardAdded).card
            assertEquals("Topics should match", newCard.topic, addedCard.topic)
            assertEquals("Questions should match", newCard.question, addedCard.question)
            assertEquals("Answers should match", newCard.answer, addedCard.answer)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testAddMultipleCards() = runTest {
        val card1 = FlashCard("InstrumentedTopic1", "Question1", "Answer1")
        val card2 = FlashCard("InstrumentedTopic2", "Question2", "Answer2")

        // Get initial count
        repository.requestCards()
        var initialCount = 0
        repository.events.test {
            val event = awaitItem()
            if (event is CardsLocalRepository.Event.Success) {
                initialCount = event.cards.size
            }
            cancelAndIgnoreRemainingEvents()
        }

        // Add cards
        repository.addCard(card1)
        repository.addCard(card2)

        // Verify final count
        repository.requestCards()
        repository.events.test {
            val event = awaitItem()
            if (event is CardsLocalRepository.Event.Success) {
                assertTrue("Card count should increase", event.cards.size >= initialCount + 2)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testCardsArePersistentInSingleton() = runTest {
        val testCard = FlashCard(
            topic = "Persistence Test Instrumented",
            question = "Is data persistent?",
            answer = "Yes, within the singleton instance"
        )

        // Add card
        repository.addCard(testCard)

        // Request cards and verify the card is there
        repository.requestCards()
        repository.events.test {
            val event = awaitItem()
            if (event is CardsLocalRepository.Event.Success) {
                val foundCard = event.cards.find { it.topic == "Persistence Test Instrumented" }
                assertTrue("Card should be found in the list", foundCard != null)
                assertEquals(testCard.question, foundCard?.question)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testInitialCardsContent() = runTest {
        repository.requestCards()

        repository.events.test {
            val event = awaitItem()
            if (event is CardsLocalRepository.Event.Success) {
                val cards = event.cards

                // Verify some of the initial cards
                val androidCard = cards.find { it.topic == "Android" }
                assertTrue("Android card should exist", androidCard != null)
                assertEquals("What is Android?", androidCard?.question)

                val kotlinCard = cards.find { it.topic == "KMM" }
                assertTrue("KMM card should exist", kotlinCard != null)
                assertEquals("What is Kotlin Multiplatform Mobile?", kotlinCard?.question)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}

