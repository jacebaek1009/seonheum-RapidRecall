package com.example.seonheum_rapidrecall


/**
 * Purpose: Generate a randpme sequence of numbers given the length
 * Design Rationale: It is kept seperate from the UI so that the user only ask for the sequence
 * but does not know how it is made
 * Outstanding issues: None
 */
class RandomSequence(
    private val length: Int,
    private val range: IntRange = 0..9,
) {
    fun generate(): List<Int> = List(length) { range.random() }
}