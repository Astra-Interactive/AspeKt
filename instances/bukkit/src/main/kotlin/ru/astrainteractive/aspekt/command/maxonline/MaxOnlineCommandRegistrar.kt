package ru.astrainteractive.aspekt.command.maxonline

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.runs

class MaxOnlineCommandRegistrar {
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("maxonline") {
            argument("online", IntegerArgumentType.integer()) { onlineArg ->
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.MAX_ONLINE)
                    val sender = ctx.source.sender
                    val onlineCount = ctx.requireArgument(onlineArg)
                    sender.sendMessage("Max online now is $onlineCount")
                    Bukkit.getServer().maxPlayers = onlineCount
                }
            }
        }.build()
    }
}
