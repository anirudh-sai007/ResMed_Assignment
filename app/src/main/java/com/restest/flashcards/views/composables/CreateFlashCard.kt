package com.restest.flashcards.views.composables

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.restest.flashcards.views.navigation.Screen

@Composable
fun CreateFlashCard(
    viewModel: CreateCardsViewModel,
    navController: NavController
) {
    val topic = remember { mutableStateOf("") }
    val question = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = topic.value,
            onValueChange = { topic.value = it },
            label = { Text("Topic") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = question.value,
            onValueChange = { question.value = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.addCard(topic.value, question.value, description.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect() { event ->
                when (event) {
                    is CreateCardsViewModel.Event.CardAdded -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }

                    CreateCardsViewModel.Event.Idle -> {
                        // Do nothing
                    }
                }
            }
        }


    }

}

fun NavGraphBuilder.createCardRoute(navController: NavController) {
    composable(Screen.CreateCard.route) {
        CreateFlashCard(viewModel(), navController)
    }
}