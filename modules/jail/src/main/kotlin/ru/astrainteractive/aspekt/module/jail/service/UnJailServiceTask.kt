package ru.astrainteractive.aspekt.module.jail.service

import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.offlinePlayer
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.time.Instant

internal class UnJailServiceTask(
    private val jailCache: JailCache,
    private val jailRepository: JailRepository,
    private val jailController: JailController,
    translationKrate: CachedKrate<PluginTranslation>
) : ServiceTask {
    private val translation by translationKrate

    private fun JailInmate.isSentenceOver(): Boolean {
        val servedSeconds = Instant.now().epochSecond - start.epochSecond
        return servedSeconds > duration.inWholeSeconds
    }

    private suspend fun free(inmate: JailInmate) {
        jailRepository.free(inmate.uuid)
        jailCache.cache(inmate.uuid)
        jailController.free(inmate)
        inmate.offlinePlayer.sendMessage(translation.jails.released)
    }

    override suspend fun execute() {
        jailRepository.getInmates()
            .getOrNull()
            .orEmpty()
            .filter { inmate -> inmate.isSentenceOver() }
            .forEach { inmate -> free(inmate) }
    }
}
