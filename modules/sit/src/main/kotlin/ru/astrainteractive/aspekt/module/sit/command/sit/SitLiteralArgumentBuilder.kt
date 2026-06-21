package ru.astrainteractive.aspekt.module.sit.command.sit

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astralibs.server.util.asBukkitLocation
import ru.astrainteractive.klibs.mikro.core.util.tryCast

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
                    val onlineKPlayer = ctx.requirePlayer()

                    @Suppress("MaxLineLength")
                    val player = onlineKPlayer
                        .tryCast<BukkitOnlineKPlayer>()
                        ?.instance
                        ?: error(
                            "Could not convert OnlineKPlayer into MinecraftOnlineKPlayer. " +
                                "This should not happen. Contact developer."
                        )

                    @Suppress("MagicNumber")
                    sitController.toggleSitPlayer(
                        player = player,
                        locationWithOffset = onlineKPlayer.getLocation()
                            .asBukkitLocation()
                            .add(0.0, -2.0, 0.0)
                    )
                }
            }
        }
    }
}
