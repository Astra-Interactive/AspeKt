package ru.astrainteractive.aspekt.command.tellchat

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc

class TellChatCommandRegistrar : KyoriComponentSerializer by KyoriComponentSerializer.Legacy {
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("tellchat") {
            argument("target", StringArgumentType.string()) { targetArg ->
                hints {
                    buildList {
                        add("*")
                        addAll(Bukkit.getOnlinePlayers().map(Player::getName))
                    }
                }
                argument("message", StringArgumentType.greedyString()) { messageArg ->
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.TELL_CHAT)
                        val target = ctx.requireArgument(targetArg)
                        val message = ctx
                            .requireArgument(messageArg)
                            .let(StringDesc::Raw)
                        when (target) {
                            "*" -> {
                                Bukkit.getOnlinePlayers().forEach { player ->
                                    player.sendMessage(message.component)
                                }
                            }

                            else -> {
                                val targetPlayer = OnlinePlayerArgumentConverter.transform(target)
                                targetPlayer.sendMessage(message.component)
                            }
                        }
                    }
                }
            }
        }.build()
    }
}
