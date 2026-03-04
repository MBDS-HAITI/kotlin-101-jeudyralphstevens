package com.kotlin.one

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()

fun runTest(name: String, result: Any, expected: Any) {
    if (result == expected) {
        println("$name → $result")
    } else {
        println("$name → got $result, expected $expected")
    }
}

// Exercise 1 — Immutable List
fun ex1CreateImmutableList(): List<Int> {
    return listOf(1, 2, 3, 4, 5)
}

// Exercise 2 — Mutable List
fun ex2CreateMutableList(): List<String> {
    val list = mutableListOf("Kotlin", "Java", "Python")
    list.add("Swift")
    return list
}

// Exercise 3 — Filter Even
fun ex3FilterEvenNumbers(): List<Int> {
    return (1..10).filter { it % 2 == 0 }
}

// Exercise 4 — Filter and Map Ages
fun ex4FilterAndMapAges(ages: List<Int>): List<String> {
    return ages
        .filter { it >= 18 }
        .map { "Adult: $it" }
}

// Exercise 5 — Flatten Nested Lists
fun ex5FlattenList(): List<Int> {
    val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    return nested.flatten()
}

// Exercise 6 — FlatMap
fun ex6FlatMapWords(): List<String> {
    val phrases = listOf("Kotlin is fun", "I love lists")
    return phrases.flatMap { it.split(" ") }
}

// Exercise 7 — Eager Processing
@OptIn(ExperimentalTime::class)
fun ex7EagerProcessing(): List<Int> {
    val start = currentTimeMs()
    val result = (1..1_000_000)
        .filter { it % 3 == 0 }
        .map { it * it }
        .take(5)
    val end = currentTimeMs()
    println("Eager  time: ${end - start} ms")
    return result
}

// Exercise 8 — Lazy Processing
@OptIn(ExperimentalTime::class)
fun ex8LazyProcessing(): List<Int> {
    val start = currentTimeMs()
    val result = (1..1_000_000)
        .asSequence()
        .filter { it % 3 == 0 }
        .map { it * it }
        .take(5)
        .toList()
    val end = currentTimeMs()
    println("Lazy   time: ${end - start} ms")
    return result
}

// Exercise 9 — Chain multiple operations
fun ex9FilterAndSortNames(names: List<String>): List<String> {
    return names
        .filter { it.startsWith("A") }
        .map { it.uppercase() }
        .sorted()
}

@OptIn(ExperimentalTime::class)
fun main() {
    println("🔍 Running List Processing Tests...\n")

    // Exercise 1
    runTest(
        name     = "ex1 - Immutable list of 5 integers",
        result   = ex1CreateImmutableList().size,
        expected = 5
    )

    // Exercise 2
    runTest(
        name     = "ex2 - Mutable list with 4 elements",
        result   = ex2CreateMutableList().size,
        expected = 4
    )

    // Exercise 3
    runTest(
        name     = "ex3 - Even numbers from 1 to 10",
        result   = ex3FilterEvenNumbers(),
        expected = listOf(2, 4, 6, 8, 10)
    )

    // Exercise 4
    runTest(
        name     = "ex4 - Filter and map ages",
        result   = ex4FilterAndMapAges(listOf(12, 17, 25, 30, 15, 22)),
        expected = listOf("Adult: 25", "Adult: 30", "Adult: 22")
    )

    // Exercise 5
    runTest(
        name     = "ex5 - Flatten nested list",
        result   = ex5FlattenList(),
        expected = listOf(1, 2, 3, 4, 5)
    )

    // Exercise 6
    runTest(
        name     = "ex6 - FlatMap words",
        result   = ex6FlatMapWords(),
        expected = listOf("Kotlin", "is", "fun", "I", "love", "lists")
    )

    // Exercise 7 & 8 — compare eager vs lazy
    val eagerResult = ex7EagerProcessing()
    val lazyResult  = ex8LazyProcessing()

    runTest(
        name     = "ex7 - Eager first 5 squares divisible by 3",
        result   = eagerResult,
        expected = listOf(9, 36, 81, 144, 225)
    )
    runTest(
        name     = "ex8 - Lazy first 5 squares divisible by 3",
        result   = lazyResult,
        expected = listOf(9, 36, 81, 144, 225)
    )

    // Exercise 9
    runTest(
        name     = "ex9 - Filter, uppercase, sort names starting with A",
        result   = ex9FilterAndSortNames(listOf("Alice", "Bob", "Anna", "Charlie", "Alex")),
        expected = listOf("ALEX", "ALICE", "ANNA")
    )

    println("\n🎯 All tests done!")
}
