package com.restest.flashcards.views.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.views.navigation.Screen

@Composable
fun FlashCardList(viewModel: CardsViewModel, navController: NavController) {
    val cards by viewModel.cardList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var cardToDelete by remember { mutableStateOf<FlashCard?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cards.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 48.dp, bottom = 80.dp)
            ) {
                items(cards) { flashCard ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = flashCard.topic,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = flashCard.question,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        }
                        IconButton(onClick = { cardToDelete = flashCard }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete flashcard",
                                tint = Color.Red
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (cards.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading flashcards...")
                    }
                    errorMessage != null -> {
                        Text(
                            "Unable to Load Flashcards",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            errorMessage.orEmpty(),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = viewModel::retryLoading) { Text("Retry") }
                    }
                    else -> {
                        Text(
                            "No Flashcards Available",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add a flashcard to get started.")
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate(Screen.FlashCards.route) },
            enabled = cards.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("Start")
        }

        IconButton(
            onClick = { navController.navigate(Screen.CreateCard.route) },
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create")
        }
    }

    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Delete Flashcard") },
            text = { Text("Delete the ${card.topic} flashcard?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCard(card)
                    cardToDelete = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

fun NavGraphBuilder.homeScreenRoute(navController: NavController) {
    composable(Screen.Home.route) {
        FlashCardList(viewModel = viewModel(), navController = navController)
    }
}
