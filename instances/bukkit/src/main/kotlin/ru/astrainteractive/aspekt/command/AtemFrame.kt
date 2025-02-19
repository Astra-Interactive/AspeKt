package ru.astrainteractive.aspekt.command

import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.BooleanArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.DoubleArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.findArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

// atemframe isVisible isFixed radius
fun CommandManager.atemFrameTabCompleter() =
    plugin.getCommand("atemframe")?.setTabCompleter { sender, command, label, args ->
        when (args.size) {
            1 -> listOf("isVisible")
            2 -> listOf("isFixed")
            3 -> listOf("radius")
            else -> emptyList()
        }
    }

fun CommandManager.atemFrame() = plugin.setCommandExecutor(
    alias = "atemframe",
    commandExecutor = commandExecutor@{ ctx ->
        ctx.requirePermission(PluginPermission.AtemFrame)

        val player = ctx.sender as? Player ?: return@commandExecutor

        val isVisible = ctx.findArgument(0, BooleanArgumentType) ?: true
        val isFixed = ctx.findArgument(1, BooleanArgumentType) ?: true
        val radius = ctx.findArgument(2, DoubleArgumentType) ?: 20.0
        val itemFrames = player.location.getNearbyEntitiesByType(ItemFrame::class.java, radius)
        player.sendMessage(
            "Params: isVisible: $isVisible; isFixed: $isFixed; radius: $radius; changed ${itemFrames.size} frames"
        )
        itemFrames.forEach {
            it.isFixed = isFixed
            it.isVisible = isVisible
        }
    },
    errorHandler = { ctx, throwable -> }
)