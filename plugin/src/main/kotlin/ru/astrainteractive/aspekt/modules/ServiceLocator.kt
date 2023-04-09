package ru.astrainteractive.aspekt.modules

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.aspekt.events.discord.DiscordEvent
import ru.astrainteractive.aspekt.events.discord.controllers.DiscordController
import ru.astrainteractive.aspekt.events.discord.controllers.LuckPermsController
import ru.astrainteractive.aspekt.events.discord.controllers.RoleController
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.events.sort.SortController
import ru.astrainteractive.aspekt.plugin.Files
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation

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