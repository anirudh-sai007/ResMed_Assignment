package com.restest.flashcards.data.repository

import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.data.remote.FlashcardResponseMapper
import com.restest.flashcards.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class CardsLocalRepository private constructor() {
    private val cards = mutableListOf<FlashCard>()
    private var defaultsLoaded = false
    private val loadMutex = Mutex()

    private val _events = MutableStateFlow<Event>(Event.Idle)
    val events = _events.asStateFlow()

    fun getCards(): List<FlashCard> = cards.toList()

    fun requestCards() {
        _events.value = Event.Success(getCards())
    }

    // Returns successfully fetched cards; throws on network/JSON errors so
    // the ViewModel can show an error and offer Retry.
    suspend fun loadDefaultCards(): List<FlashCard> = loadMutex.withLock {
        if (!defaultsLoaded) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getFlashcards()
            }
            val fetched = FlashcardResponseMapper.map(response)
            cards.clear()
            cards.addAll(fetched)
            defaultsLoaded = true
        }
        val snapshot = getCards()
        _events.value = Event.Success(snapshot)
        snapshot
    }

    fun addCard(newCard: FlashCard) {
        cards.add(newCard)
        _events.value = Event.CardAdded(newCard)
    }

    fun deleteCard(card: FlashCard): List<FlashCard> {
        if (cards.remove(card)) {
            _events.value = Event.Success(getCards())
        }
        return getCards()
    }

    companion object {
        val instance: CardsLocalRepository by lazy { CardsLocalRepository() }
    }

    sealed class Event {
        data class Success(val cards: List<FlashCard>) : Event()
        data class CardAdded(val card: FlashCard) : Event()
        data class Error(val message: String) : Event()
        data object Idle : Event()
    }
}

