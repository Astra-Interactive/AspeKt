package ru.astrainteractive.aspekt.module.menu.command.invclose

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.runs

/**
 * InvClose command registrar.
 * Mirrors legacy behavior:
 * - /invclose [player]
 * - If player provided, closes their inventory.
 * - If no args, does nothing.
 */
internal class InvCloseCommandRegistrar {
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("invclose") {
            argument("player", StringArgumentType.string()) { playerArg ->
                hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                runs { ctx ->
                    ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                        .closeInventory()
                }
            }
        }.build()
    }
}
