package ru.astrainteractive.aspekt.module.sit.command.sit

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astralibs.server.util.asBukkitLocation
import ru.astrainteractive.klibs.mikro.core.util.cast

/**
 * Sit command registrar. Builds Brigadier node for:
 * /sit
 */
internal class SitLiteralArgumentBuilder(
    private val sitController: SitController,
    private val multiplatformCommand: MultiplatformCommand
) {
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("sit") {
                runs { ctx ->
                    val player = ctx.requirePlayer()
                    @Suppress("MagicNumber")
                    sitController.toggleSitPlayer(
                        player = player.cast<BukkitOnlineKPlayer>().instance,
                        locationWithOffset = player.getLocation()
                            .asBukkitLocation()
                            .add(0.0, -2.0, 0.0)
                    )
                }
            }
        }
    }
}
