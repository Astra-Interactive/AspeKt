package ru.astrainteractive.aspekt.module.jail.usecase

import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.toKLocation
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID

internal class TeleportInmateToJailUseCase(
    private val jailRepository: JailRepository,
    private val platformServer: PlatformServer,
    private val dispatchers: KotlinDispatchers
) : Logger by JUtiltLogger("AspeKt-TeleportInmateToJailUseCase") {
    suspend operator fun invoke(inmate: JailInmate) {
        val jail = jailRepository.getJail(inmate.jailName)
            .onFailure { throwable -> error(throwable) { "#invoke could not find jail ${inmate.jailName}" } }
            .getOrNull()
            ?: return
        val player = platformServer.findOnlinePlayer(UUID.fromString(inmate.uuid))
        if (player == null) {
            error { "#invoke could not find player ${inmate.uuid}" }
            return
        }
        withContext(dispatchers.Main) { player.teleport(jail.location.toKLocation()) }
    }
}
