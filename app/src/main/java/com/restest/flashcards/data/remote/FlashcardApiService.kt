
package com.restest.flashcards.data.remote

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Headers


// task 2
interface FlashcardApiService {

    @Headers("Content-Type: application/json")
    @GET("v1/flashcards")
    suspend fun getFlashcards(): JsonElement
}
