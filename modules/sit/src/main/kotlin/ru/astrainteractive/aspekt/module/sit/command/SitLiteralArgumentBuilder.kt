package ru.astrainteractive.aspekt.module.sit.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Location
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astralibs.server.util.asBukkitLocation
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class SitLiteralArgumentBuilder(
    private val toggleSitPlayer: (player: Player, locationWithOffset: Location) -> Unit,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("sit") {
                runs(commandExceptionHandler::onException) { ctx ->
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
                    toggleSitPlayer.invoke(
                        player,
                        onlineKPlayer.getLocation()
                            .asBukkitLocation()
                            .add(
                                0.0,
                                -2.0,
                                0.0
                            )
                    )
                }
            }
        }
    }
}
