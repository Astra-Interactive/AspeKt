package ru.astrainteractive.aspekt.module.jail.data.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal class CachedJailApiImpl(
    private val jailApi: JailApi,
    private val scope: CoroutineScope
) : CachedJailApi {
    private val cache = mutableMapOf<String, Boolean>()
    private val cachedJails = mutableListOf<Jail>()
    private val cachedInmates = mutableListOf<JailInmate>()

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

    override fun getJails(): List<Jail> {
        scope.launch {
            jailApi.getJails()
                .onSuccess { jails ->
                    cachedJails.clear()
                    cachedJails.addAll(jails)
                }
        }
        return cachedJails
    }

    override fun getInmates(): List<JailInmate> {
        scope.launch {
            jailApi.getInmates()
                .onSuccess { jailInmates ->
                    cachedInmates.clear()
                    cachedInmates.addAll(jailInmates)
                }
        }
        return cachedInmates
    }
}
