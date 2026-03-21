package ru.astrainteractive.aspekt.command.tellchat

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

class TellChatLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
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
            }
        }
    }
}
