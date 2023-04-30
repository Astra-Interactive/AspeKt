package ru.astrainteractive.aspekt.commands

import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter

// atemframe isVisible isFixed radius
fun CommandManager.atemFrameTabCompleter(plugin: JavaPlugin) = plugin.registerTabCompleter("atemframe") {
    when (this.args.size) {
        1 -> listOf("isVisible")
        2 -> listOf("isFixed")
        3 -> listOf("radius")
        else -> emptyList()
    }
}

fun CommandManager.atemFrame(plugin: JavaPlugin) = plugin.registerCommand("atemframe") {
    if (!PluginPermission.AtemFrame.hasPermission(sender)) return@registerCommand
    val player = sender as? Player ?: return@registerCommand
    val isVisible = argument(0) { it == "true" }.successOrNull()?.value ?: true
    val isFixed = argument(1) { it == "true" }.successOrNull()?.value ?: true
    val radius = argument(2) { it?.toDoubleOrNull() }.successOrNull()?.value ?: 20.0
    val itemFrames = player.location.getNearbyEntitiesByType(ItemFrame::class.java, radius)
    player.sendMessage(
        "Params: isVisible: $isVisible; isFixed: $isFixed; radius: $radius; changed ${itemFrames.size} frames"
    )
    itemFrames.forEach {
        it.isFixed = isFixed
        it.isVisible = isVisible
    }
}
