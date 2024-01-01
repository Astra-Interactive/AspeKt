package ru.astrainteractive.aspekt.module.towny.discord.util

import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object RestActionExt {
    suspend fun <T> RestAction<T>.await() = supervisorScope {
        suspendCancellableCoroutine<T> { continuation ->
            queue(continuation::resume, continuation::resumeWithException)
        }
    }

    suspend fun <T> RestAction<T>.async(): Deferred<T> = supervisorScope {
        async { await() }
    }

    suspend fun <T> RestAction<T>.asyncResult(): Deferred<Result<T>> = supervisorScope {
        async { kotlin.runCatching { await() } }
    }
}
