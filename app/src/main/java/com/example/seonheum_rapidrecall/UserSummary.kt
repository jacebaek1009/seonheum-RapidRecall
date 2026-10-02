package com.example.seonheum_rapidrecall


/**
 * Purpose: Gives the total attemps correctness, accuracy, and total attempts
 * Design Rationale: Nothing stored here, and every  calculation done in UserLog
 * Outstanding issues: None
 */
class UserSummary(private val log: UserLog) {

    val total: Int get() = log.attempts.size

    val correct: Int get() = log.attempts.count { it.isCorrect }

    val accuracyPercent: Double
        get() = if (total == 0) 0.0 else correct * 100.0 / total
}
