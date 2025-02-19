package ru.astrainteractive.aspekt.module.sit.command

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController

internal interface SitCommandDependencies {
    val plugin: JavaPlugin
    val sitController: SitController
}
