package com.example.seonheum_rapidrecall

import androidx.compose.runtime.mutableStateListOf

/**
 * Purpose: Stores a list of all of the attempts as well as a function for resetting the stats if
 * the user wants to start a new game
 * Design Rationale: The attempt list is private, so other classes can only update it, read or reset
 * it
 * Outstanding issues: The only issue is that there is no data persistance
 * If app is reset all of the data is gone
 */
class UserLog {
    private val _attempts = mutableStateListOf<Attempt>()

    val attempts: List<Attempt> get() = _attempts

    fun add(attempt: Attempt) {
        _attempts.add(attempt)
    }

    fun resetStats() {
        _attempts.clear()
    }
}
