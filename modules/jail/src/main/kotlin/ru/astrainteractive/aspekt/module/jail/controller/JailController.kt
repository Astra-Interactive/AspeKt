package ru.astrainteractive.aspekt.module.jail.controller

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.toBukkitLocation
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal class JailController(
    private val dispatchers: KotlinDispatchers,
    private val jailApi: JailApi
) : CoroutineFeature by CoroutineFeature.Default(dispatchers.Default),
    Logger by JUtiltLogger("AspeKt-JailController") {

    fun onJailed(inmate: JailInmate) {
        launch {
            val jail = jailApi.getJail(inmate.jailName)
                .onFailure { error(it) { "#onJailed could not find jail ${inmate.jailName}" } }
                .getOrNull()
                ?: return@launch
            val player = Bukkit.getPlayer(UUID.fromString(inmate.uuid)) ?: run {
                error { "#onJailed could not find player ${inmate.uuid}" }
                return@launch
            }
            withContext(dispatchers.Main) { player.teleport(jail.location.toBukkitLocation()) }
        }
    }

    fun free(inmate: JailInmate) {
        launch {
            val player = Bukkit.getPlayer(UUID.fromString(inmate.uuid)) ?: run {
                error { "#onJailed could not find player ${inmate.uuid}" }
                return@launch
            }
            jailApi.free(player.uniqueId.toString())
                .onFailure { error { "#onJailed could not free player ${inmate.uuid}" } }
                .onSuccess {
                    withContext(dispatchers.Main) { player.teleport(inmate.lastLocation.toBukkitLocation()) }
                }
        }
    }

    fun tryTeleportToJail(uuid: UUID) {
        launch {
            val inmate = jailApi.getInmate(uuid.toString())
                .getOrNull()
                ?: return@launch
            onJailed(inmate)
        }
    }
}
