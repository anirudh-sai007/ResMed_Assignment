package com.restest.flashcards.data.repository

import app.cash.turbine.test
import com.restest.flashcards.data.model.FlashCard
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CardsLocalRepositoryTest {

    private lateinit var repository: CardsLocalRepository

    @Before
    fun setup() {
        // Note: Since CardsLocalRepository is a singleton, we need to be careful
        // In a real app, you'd want to make it testable by allowing injection
        repository = CardsLocalRepository.instance
    }

    @Test
    fun `requestCards emits Success event with initial cards`() = runTest {
        repository.events.test {
            // Initial state should be Idle
            val initialEvent = awaitItem()
            assertTrue(initialEvent is CardsLocalRepository.Event.Idle)

            // Request cards
            repository.requestCards()

            // Should emit Success event with cards
            val successEvent = awaitItem()
            assertTrue(successEvent is CardsLocalRepository.Event.Success)
            
            val cards = (successEvent as CardsLocalRepository.Event.Success).cards
            assertTrue(cards.isNotEmpty())
            assertEquals(13, cards.size) // Initial cards count
            
            // Verify first card
            assertEquals("Android", cards[0].topic)
            assertEquals("What is Android?", cards[0].question)
        }
    }

    @Test
    fun `addCard adds new card and emits CardAdded event`() = runTest {
        val newCard = FlashCard(
            topic = "Kotlin",
            question = "What is Kotlin?",
            answer = "Kotlin is a modern programming language"
        )

        repository.events.test {
            // Skip initial Idle event
            awaitItem()

            // Add card
            repository.addCard(newCard)

            // Should emit CardAdded event
            val cardAddedEvent = awaitItem()
            assertTrue(cardAddedEvent is CardsLocalRepository.Event.CardAdded)
            
            val addedCard = (cardAddedEvent as CardsLocalRepository.Event.CardAdded).card
            assertEquals(newCard.topic, addedCard.topic)
            assertEquals(newCard.question, addedCard.question)
            assertEquals(newCard.answer, addedCard.answer)
        }
    }

    @Test
    fun `addCard increases card list size`() = runTest {
        repository.events.test {
            // Skip initial Idle
            awaitItem()

            // Get initial count
            repository.requestCards()
            val initialEvent = awaitItem() as CardsLocalRepository.Event.Success
            val initialCount = initialEvent.cards.size

            // Add a new card
            val newCard = FlashCard("Test", "Test Question", "Test Answer")
            repository.addCard(newCard)
            awaitItem() // CardAdded event

            // Request cards again
            repository.requestCards()
            val afterAddEvent = awaitItem() as CardsLocalRepository.Event.Success
            val afterAddCount = afterAddEvent.cards.size

            // Verify count increased
            assertEquals(initialCount + 1, afterAddCount)
        }
    }

    @Test
    fun `multiple addCard calls add all cards`() = runTest {
        val card1 = FlashCard("UniqueMulti1", "Question1", "Answer1")
        val card2 = FlashCard("UniqueMulti2", "Question2", "Answer2")
        val card3 = FlashCard("UniqueMulti3", "Question3", "Answer3")

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
        repository.addCard(card3)

        // Request cards again and verify
        repository.requestCards()
        repository.events.test {
            val event = awaitItem()
            if (event is CardsLocalRepository.Event.Success) {
                assertEquals(initialCount + 3, event.cards.size)
                // Verify the cards were actually added
                assertTrue(event.cards.any { it.topic == "UniqueMulti1" })
                assertTrue(event.cards.any { it.topic == "UniqueMulti2" })
                assertTrue(event.cards.any { it.topic == "UniqueMulti3" })
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repository singleton returns same instance`() {
        val instance1 = CardsLocalRepository.instance
        val instance2 = CardsLocalRepository.instance

        assertTrue(instance1 === instance2)
    }
}

