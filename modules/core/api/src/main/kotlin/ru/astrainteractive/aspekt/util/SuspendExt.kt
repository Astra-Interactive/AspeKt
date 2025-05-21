package ru.astrainteractive.aspekt.util

import kotlinx.coroutines.yield

suspend fun awaitForCompletion(isCompleted: suspend () -> Boolean) {
    while (!isCompleted.invoke()) {
        yield()
    }
}
