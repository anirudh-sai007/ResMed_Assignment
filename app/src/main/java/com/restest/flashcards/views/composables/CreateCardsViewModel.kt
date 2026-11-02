package com.restest.flashcards.views.composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.data.repository.CardsLocalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateCardsViewModel(private val repository: CardsLocalRepository = CardsLocalRepository.instance, private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    ViewModel() {

    private val _events: MutableStateFlow<Event> = MutableStateFlow(Event.Idle)
    val events = _events.asStateFlow()

    init {
        collectRepositoryEvents()

    }


    fun addCard(topic: String, question: String, answer: String) {
        if (topic.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            return
        }
        repository.addCard(
            FlashCard(
                topic,
                question,
                answer
            )
        )
    }

    private fun collectRepositoryEvents() {
        viewModelScope.launch(dispatcher) {
            repository.events.collect { event ->
                when (event) {
                    is CardsLocalRepository.Event.CardAdded -> {
                        _events.emit(Event.CardAdded("New card added"))
                    }

                    is CardsLocalRepository.Event.Success -> {
                    }

                    is CardsLocalRepository.Event.Error, CardsLocalRepository.Event.Idle -> {}
                }
            }
        }
    }

    sealed class Event {
        data class CardAdded(val message: String) : Event()
        data object Idle : Event()
    }
}