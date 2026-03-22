package ru.astrainteractive.aspekt.command.maxonline

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand

class MaxOnlineLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand
) {
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("maxonline") {
                argument("online", IntegerArgumentType.integer()) { onlineArg ->
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.MAX_ONLINE)
                        val sender = ctx.getSender()
                        val onlineCount = ctx.requireArgument(onlineArg)
                        sender.sendMessage(Component.text("Max online now is $onlineCount"))
                        Bukkit.getServer().maxPlayers = onlineCount
                    }
                }
            }
        }
    }
}
