package ru.astrainteractive.aspekt.command

import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginPermission
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

fun CommandManager.atemFrame() = plugin.getCommand("atemframe")?.setExecutor { sender, command, label, args ->
    if (!sender.toPermissible().hasPermission(PluginPermission.AtemFrame)) return@setExecutor true
    val player = sender as? Player ?: return@setExecutor true

    val isVisible = args.getOrNull(0)?.toBooleanStrictOrNull() ?: true
    val isFixed = args.getOrNull(1)?.toBooleanStrictOrNull() ?: true
    val radius = args.getOrNull(2)?.toDoubleOrNull() ?: 20.0
    val itemFrames = player.location.getNearbyEntitiesByType(ItemFrame::class.java, radius)
    player.sendMessage(
        "Params: isVisible: $isVisible; isFixed: $isFixed; radius: $radius; changed ${itemFrames.size} frames"
    )
    itemFrames.forEach {
        it.isFixed = isFixed
        it.isVisible = isVisible
    }
    true
}
