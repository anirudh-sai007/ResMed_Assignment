
package com.restest.flashcards.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// task 2
object RetrofitClient {

    private const val BASE_URL =
        "https://resflash.free.beeceptor.com/"

    val api: FlashcardApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(FlashcardApiService::class.java)
    }
}
