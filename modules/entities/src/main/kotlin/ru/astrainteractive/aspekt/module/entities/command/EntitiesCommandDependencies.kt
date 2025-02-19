package ru.astrainteractive.aspekt.module.entities.command

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.entities.gui.Router

internal interface EntitiesCommandDependencies {
    val plugin: JavaPlugin
    val router: Router
}
