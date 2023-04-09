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
    val pluginConfigModule = reloadable {
        PluginConfiguration(Files.configFile.fileConfiguration)
    }
    val TranslationModule = reloadable {
        PluginTranslation()
    }
    val discordEventModule = module {
        Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@module null
        Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@module null
        DiscordEvent(
            discordController = Controllers.discordControllerModule,
            luckPermsController = Controllers.luckPermsControllerModule
        )
    }
    val autoBroadcastModule = module {
        AutoBroadcast(
            config = pluginConfigModule
        )
    }

    object Controllers {
        val discordControllerModule = module {
            DiscordController(
                pluginConfiguration = pluginConfigModule
            ) as RoleController
        }
        val luckPermsControllerModule = module {
            LuckPermsController(
                pluginConfiguration = pluginConfigModule
            )as RoleController
        }
        val sitControllerModule = module {
            SitController(TranslationModule, pluginConfigModule)
        }
        val sortControllerModule = module {
            SortController()
        }
    }
}