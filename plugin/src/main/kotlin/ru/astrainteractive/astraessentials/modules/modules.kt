package ru.astrainteractive.astraessentials.modules

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astraessentials.events.EventHandler
import ru.astrainteractive.astraessentials.events.discord.DiscordEvent
import ru.astrainteractive.astraessentials.utils.Files
import ru.astrainteractive.astraessentials.utils.PluginConfiguration
import ru.astrainteractive.astraessentials.utils.PluginTranslation

val PluginConfigModule = reloadable {
    PluginConfiguration(Files.configFile.fileConfiguration)
}
val TranslationModule = reloadable {
    PluginTranslation()
}
val discordEventModule = module {
    Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@module null
    Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@module null
    DiscordEvent()
}