package ru.astrainteractive.aspekt.module.menu.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand

internal class InvCloseLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("invclose") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs(commandExceptionHandler::onException) { ctx ->
                        ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                            .closeInventory()
                    }
                }
            }
        }
    }
}
