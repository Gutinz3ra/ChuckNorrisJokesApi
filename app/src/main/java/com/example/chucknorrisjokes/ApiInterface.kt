package com.example.chucknorrisjokes

import retrofit2.Response
import retrofit2.http.GET

interface ChuckNorrisApi {
    @GET("jokes/random")
    suspend fun getRandomJoke(): Response<Joke>
}