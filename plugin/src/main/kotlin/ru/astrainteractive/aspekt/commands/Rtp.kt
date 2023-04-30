package ru.astrainteractive.aspekt.commands

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.hex

fun CommandManager.rtp(plugin: JavaPlugin) = plugin.registerCommand("rtp") {
    sender.sendMessage("#db2c18Возможно, вы хотели ввести /tpr".hex())
}
