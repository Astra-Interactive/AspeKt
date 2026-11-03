package ru.astrainteractive.aspekt.module.jail.service

import ru.astrainteractive.aspekt.core.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.usecase.FreeInmateUseCase
import ru.astrainteractive.aspekt.module.jail.util.isSentenceOver
import ru.astrainteractive.aspekt.module.jail.util.offlinePlayer
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.time.Clock

internal class UnJailServiceTask(
    private val jailCache: JailCache,
    private val jailRepository: JailRepository,
    private val freeInmateUseCase: FreeInmateUseCase,
    private val clock: Clock,
    translationKrate: CachedKrate<PluginTranslation>
) : ServiceTask, Logger by JUtiltLogger("AspeKt-UnJailServiceTask") {
    private val translation by translationKrate

    private suspend fun free(inmate: JailInmate) {
        freeInmateUseCase.invoke(inmate)
            .onFailure { throwable -> error(throwable) { "#free could not free ${inmate.uuid}" } }
        jailCache.cache(inmate.uuid)
        inmate.offlinePlayer.sendMessage(translation.jails.released)
    }

    override suspend fun execute() {
        jailRepository.findInmates()
            .getOrNull()
            .orEmpty()
            .filter { inmate -> inmate.isSentenceOver(clock.now()) }
            .forEach { inmate -> free(inmate) }
    }
}
