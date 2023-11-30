package com.example.chucknorrisjokes

import kotlinx.serialization.Serializable

@Serializable
data class Joke(
    val id: String,
    val value: String
)