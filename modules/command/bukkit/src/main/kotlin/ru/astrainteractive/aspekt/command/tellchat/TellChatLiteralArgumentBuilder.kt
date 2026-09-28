package ru.astrainteractive.aspekt.command.tellchat

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.core.command.CommandExceptionHandler
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.localization.markup.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class TellChatLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val commandExceptionHandler: CommandExceptionHandler,
) {
    private val kyori by kyoriKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tellchat") {
                argument("target", StringArgumentType.string()) { targetArg ->
                    hints {
                        buildList {
                            add("*")
                            addAll(Bukkit.getOnlinePlayers().map(Player::getName))
                        }
                    }
                    argument("message", StringArgumentType.greedyString()) { messageArg ->
                        runs(commandExceptionHandler::handle) { ctx ->
                            ctx.requirePermission(PluginPermission.TELL_CHAT)
                            val target = ctx.requireArgument(targetArg)
                            val message = ctx
                                .requireArgument(messageArg)
                                .let(kyori::toComponent)
                            when (target) {
                                "*" -> {
                                    Bukkit.getOnlinePlayers().forEach { player ->
                                        player.sendMessage(message)
                                    }
                                }

                                else -> {
                                    val targetPlayer = OnlinePlayerArgumentConverter.transform(target)
                                    targetPlayer.sendMessage(message)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
