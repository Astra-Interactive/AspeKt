package ru.astrainteractive.aspekt.module.jail.usecase

import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.toKLocation
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal class FreeInmateUseCase(
    private val jailRepository: JailRepository,
    private val platformServer: PlatformServer,
    private val dispatchers: KotlinDispatchers
) {
    suspend operator fun invoke(inmate: JailInmate): Result<Unit> {
        val result = jailRepository.free(inmate.uuid)
        if (result.isFailure) return result
        val player = platformServer.findOnlinePlayer(UUID.fromString(inmate.uuid)) ?: return result
        withContext(dispatchers.Main) { player.teleport(inmate.lastLocation.toKLocation()) }
        return result
    }
}
