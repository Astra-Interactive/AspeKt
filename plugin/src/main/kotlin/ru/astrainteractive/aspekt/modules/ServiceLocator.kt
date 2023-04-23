package ru.astrainteractive.aspekt.modules

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.aspekt.events.discord.DiscordEvent
import ru.astrainteractive.aspekt.events.discord.controllers.DiscordController
import ru.astrainteractive.aspekt.events.discord.controllers.LuckPermsController
import ru.astrainteractive.aspekt.events.discord.controllers.RoleController
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.events.sort.SortController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager

object ServiceLocator {
    private val plugin by AspeKt
    val bukkitDispatchers = module {
        DefaultBukkitDispatchers(plugin)
    }
    val configFileManager = module {
        DefaultSpigotFileManager(plugin,"config.yml")
    }
    val pluginConfigModule = reloadable {
        PluginConfiguration(configFileManager.value.fileConfiguration)
    }
    val TranslationModule = reloadable {
        PluginTranslation(plugin)
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
            config = pluginConfigModule,
            bukkitDispatchers = bukkitDispatchers.value
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