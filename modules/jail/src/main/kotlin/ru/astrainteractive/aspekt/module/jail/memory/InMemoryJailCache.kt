package ru.astrainteractive.aspekt.module.jail.memory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal class InMemoryJailCache(
    private val jailRepository: JailRepository,
    private val scope: CoroutineScope
) : JailCache {
    private val cache = mutableMapOf<String, Boolean>()
    private val cachedJails = mutableListOf<Jail>()
    private val cachedInmates = mutableListOf<JailInmate>()

    override fun isInJail(uuid: String): Boolean {
        return cache.getOrDefault(uuid, false)
    }

    override fun cache(uuid: String) {
        scope.launch {
            val isInmate = jailRepository.getInmate(uuid).getOrNull() != null
            cache[uuid] = isInmate
        }
    }

    override fun forget(uuid: String) {
        cache.remove(uuid)
    }

    override fun getJails(): List<Jail> {
        scope.launch {
            jailRepository.getJails()
                .onSuccess { jails ->
                    cachedJails.clear()
                    cachedJails.addAll(jails)
                }
        }
        return cachedJails
    }

    override fun getInmates(): List<JailInmate> {
        scope.launch {
            jailRepository.getInmates()
                .onSuccess { jailInmates ->
                    cachedInmates.clear()
                    cachedInmates.addAll(jailInmates)
                }
        }
        return cachedInmates
    }
}
