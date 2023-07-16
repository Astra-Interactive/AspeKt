package ru.astrainteractive.aspekt.command

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.adminprivate.models.ChunkFlag
import ru.astrainteractive.aspekt.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.utils.withEntry

/**
 * /adminprivate claim
 * /adminprivate unclaim
 * /adminprivate flag <flag> <bool>
 */
fun CommandManager.adminPrivate() = plugin.registerCommand("adminprivate") {
    val player = (sender as? Player) ?: run {
        sender.sendMessage(translation.onlyPlayerCommand)
        return@registerCommand
    }
    if (!PluginPermission.AdminClaim.hasPermission(player)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    when (args.getOrNull(0)) {
        "claim" -> pluginScope.launch(dispatchers.IO) {
            runCatching {
                adminPrivateController.claim(player.chunk.adminChunk)
            }.onSuccess {
                sender.sendMessage(translation.chunkClaimed)
            }.onFailure {
                it.printStackTrace()
                sender.sendMessage(translation.error)
            }
        }

        "unclaim" -> pluginScope.launch(dispatchers.IO) {
            runCatching {
                adminPrivateController.unclaim(player.chunk.adminChunk)
            }.onSuccess {
                sender.sendMessage(translation.chunkUnClaimed)
            }.onFailure {
                it.printStackTrace()
                sender.sendMessage(translation.error)
            }
        }

        "flag" -> pluginScope.launch(dispatchers.IO) {
            runCatching {
                val flag = argument(1) { it?.let(ChunkFlag::valueOf) }.onFailure {
                    sender.sendMessage(translation.wrongUsage)
                }.successOrNull()?.value ?: return@launch

                val value = argument(2) { it.toBoolean() }.onFailure {
                    sender.sendMessage(translation.wrongUsage)
                }.successOrNull()?.value ?: return@launch
                adminPrivateController.setFlag(
                    flag = flag,
                    value = value,
                    chunk = player.chunk.adminChunk
                )
            }.onSuccess {
                sender.sendMessage(translation.chunkFlagChanged)
            }.onFailure {
                it.printStackTrace()
                sender.sendMessage(translation.error)
            }
        }

        else -> sender.sendMessage(translation.wrongUsage)
    }
}

fun CommandManager.adminPrivateCompleter() = plugin.registerTabCompleter("adminprivate") {
    when {
        args.size <= 1 -> listOf("claim", "unclaim", "flag").withEntry(args.getOrNull(0))
        args.getOrNull(0) == "flag" -> when (args.size) {
            2 -> ChunkFlag.values().map(ChunkFlag::toString).withEntry(args.getOrNull(1))
            3 -> listOf("true", "false").withEntry(args.getOrNull(2))
            else -> emptyList()
        }

        else -> emptyList()
    }
}
