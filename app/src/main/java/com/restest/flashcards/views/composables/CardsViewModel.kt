package com.restest.flashcards.views.composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.data.repository.CardsLocalRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardsViewModel(
    private val repository: CardsLocalRepository = CardsLocalRepository.instance,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _cardList = MutableStateFlow<List<FlashCard>>(repository.getCards())
    val cardList = _cardList.asStateFlow()


//    task 3
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        collectRepositoryEvents()
        loadCards()
    }

    private fun collectRepositoryEvents() {
        viewModelScope.launch(dispatcher) {
            repository.events.collect { event ->
                when (event) {
                    is CardsLocalRepository.Event.Success -> _cardList.value = event.cards
                    is CardsLocalRepository.Event.CardAdded -> _cardList.value = repository.getCards()
                    is CardsLocalRepository.Event.Error -> _errorMessage.value = event.message
                    CardsLocalRepository.Event.Idle -> Unit
                }
            }
        }
    }


//    task 3
    private fun loadCards() {
        if (_isLoading.value) return
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                _cardList.value = repository.loadDefaultCards()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value =
                    "Unable to load flashcards. Check your internet connection and try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

//    task 3
    fun retryLoading() = loadCards()


    // task 1
    fun deleteCard(card: FlashCard) {
        _cardList.value = repository.deleteCard(card)
    }
}
