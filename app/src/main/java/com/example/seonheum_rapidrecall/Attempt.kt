package com.example.seonheum_rapidrecall

/**
 * Purpose: After a round Stores the sequence length, user input, the target sequenc, and the timestamp
 * Design Rationale: It is kept seperate from the UI so its just stores the data
 * Outstanding issues: None
 */
data class Attempt(
    val length: Int,
    val userInput: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val isCorrect: Boolean get() = userInput == target
}