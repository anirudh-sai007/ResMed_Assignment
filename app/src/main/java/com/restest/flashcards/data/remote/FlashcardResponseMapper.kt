
package com.restest.flashcards.data.remote


// task 2

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.restest.flashcards.data.model.FlashCard

object FlashcardResponseMapper {

    fun map(response: JsonElement): List<FlashCard> {

        val array = when {
            response.isJsonArray -> response.asJsonArray

            response.isJsonObject -> {
                val root = response.asJsonObject

                val flashcards = root.get("flashcards")

                require(
                    flashcards != null &&
                            flashcards.isJsonArray
                ) {
                    "Invalid API response: flashcards list missing"
                }

                flashcards.asJsonArray
            }

            else -> error("Unexpected API response")
        }

        return array.map { element ->

            require(element.isJsonObject) {
                "Invalid flashcard object"
            }

            val item = element.asJsonObject

            FlashCard(
                topic = item.requiredText("slug")
                    .replace("-", " ")
                    .split(" ")
                    .joinToString(" ") {
                        it.replaceFirstChar { char ->
                            char.uppercase()
                        }
                    },
                question = item.requiredText("question"),
                answer = item.requiredText("answer")
            )
        }
    }

    private fun JsonObject.requiredText(
        key: String
    ): String {
        val value = get(key)

        require(
            value != null &&
                    value.isJsonPrimitive &&
                    value.asJsonPrimitive.isString
        ) {
            "Missing or invalid field: $key"
        }

        return value.asString
    }
}
