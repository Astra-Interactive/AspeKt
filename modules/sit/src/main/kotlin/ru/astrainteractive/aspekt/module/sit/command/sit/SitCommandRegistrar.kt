package ru.astrainteractive.aspekt.module.sit.command.sit

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs

/**
 * Sit command registrar. Builds Brigadier node for:
 * /sit
 */
internal class SitCommandRegistrar(
    private val sitController: SitController
) {
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("sit") {
            runs { ctx ->
                val player: Player = ctx.requirePlayer()
                sitController.toggleSitPlayer(
                    player = player,
                    locationWithOffset = player.location.add(0.0, -2.0, 0.0)
                )
            }
        }.build()
    }
}
