package ru.astrainteractive.aspekt.module.entities.command

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.entities.gui.Router
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

interface EntitiesCommandDependencies {
    val plugin: JavaPlugin
    val router: Router
}

fun EntitiesCommandDependencies.entities() =
    plugin.getCommand("entities")?.setExecutor { sender, command, label, args ->
        if (!sender.toPermissible().hasPermission(PluginPermission.Entities)) return@setExecutor true
        val player = sender as? Player ?: return@setExecutor true
        router.open(Router.Route.Entities(player))
        true
    }
