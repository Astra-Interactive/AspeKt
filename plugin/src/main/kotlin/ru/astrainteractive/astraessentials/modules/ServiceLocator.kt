package ru.astrainteractive.astraessentials.modules

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astraessentials.events.discord.DiscordEvent
import ru.astrainteractive.astraessentials.events.discord.controllers.DiscordController
import ru.astrainteractive.astraessentials.events.discord.controllers.LuckPermsController
import ru.astrainteractive.astraessentials.events.discord.controllers.RoleController
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astraessentials.events.sort.SortController
import ru.astrainteractive.astraessentials.plugin.Files
import ru.astrainteractive.astraessentials.plugin.PluginConfiguration
import ru.astrainteractive.astraessentials.plugin.PluginTranslation

object ServiceLocator {
    val PluginConfigModule = reloadable {
        PluginConfiguration(Files.configFile.fileConfiguration)
    }
    val TranslationModule = reloadable {
        PluginTranslation()
    }
    val discordEventModule = module {
        Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@module null
        Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@module null
        DiscordEvent(
            discordController = Controllers.discordController,
            luckPermsController = Controllers.luckPermsController
        )
    }
    val autoBroadcast = module {
        AutoBroadcast(
            config = PluginConfigModule
        )
    }

    object Controllers {
        val discordController = module {
            DiscordController(
                pluginConfiguration = PluginConfigModule
            ) as RoleController
        }
        val luckPermsController = module {
            LuckPermsController(
                pluginConfiguration = PluginConfigModule
            )as RoleController
        }
        val sitController = module {
            SitController(TranslationModule)
        }
        val sortController = module {
            SortController()
        }
    }
}