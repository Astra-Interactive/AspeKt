package ru.astrainteractive.aspekt.module.jail.service

import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.offlinePlayer
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.time.Instant

internal class UnJailServiceTask(
    private val cachedJailApi: CachedJailApi,
    private val jailApi: JailApi,
    private val jailController: JailController,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>
) : ServiceTask,
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    private fun JailInmate.isSentenceOver(): Boolean {
        val servedSeconds = Instant.now().epochSecond - start.epochSecond
        return servedSeconds > duration.inWholeSeconds
    }

    private suspend fun free(inmate: JailInmate) {
        jailApi.free(inmate.uuid)
        cachedJailApi.cache(inmate.uuid)
        jailController.free(inmate)
        inmate.offlinePlayer.sendMessage(translation.jails.youVeBeenFreed.component)
    }

    override suspend fun execute() {
        jailApi.getInmates()
            .getOrNull()
            .orEmpty()
            .filter { inmate -> inmate.isSentenceOver() }
            .forEach { inmate -> free(inmate) }
    }
}
