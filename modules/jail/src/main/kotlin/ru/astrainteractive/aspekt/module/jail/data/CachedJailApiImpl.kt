package ru.astrainteractive.aspekt.module.jail.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class CachedJailApiImpl(
    private val jailApi: JailApi,
    private val scope: CoroutineScope
) : CachedJailApi {
    private val cache = mutableMapOf<String, Boolean>()

    override fun isInJail(uuid: String): Boolean {
        return cache.getOrDefault(uuid, false)
    }

    override fun cache(uuid: String) {
        scope.launch {
            val isInmate = jailApi.getInmate(uuid).getOrNull() != null
            cache[uuid] = isInmate
        }
    }

    override fun forget(uuid: String) {
        cache.remove(uuid)
    }
}
