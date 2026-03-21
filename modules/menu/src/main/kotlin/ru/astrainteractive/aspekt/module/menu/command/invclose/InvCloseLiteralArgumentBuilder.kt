package ru.astrainteractive.aspekt.module.menu.command.invclose

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand

/**
 * InvClose command registrar.
 * Mirrors legacy behavior:
 * - /invclose [player]
 * - If player provided, closes their inventory.
 * - If no args, does nothing.
 */
internal class InvCloseLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand
) {
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("invclose") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs { ctx ->
                        ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                            .closeInventory()
                    }
                }
            }
        }
    }
}
