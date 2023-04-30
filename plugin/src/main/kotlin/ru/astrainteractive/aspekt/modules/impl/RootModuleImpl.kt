package ru.astrainteractive.aspekt.modules.impl

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.events.discord.DiscordEvent
import ru.astrainteractive.aspekt.modules.RootModule
import ru.astrainteractive.aspekt.plugin.AutoBroadcastJob
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Lateinit
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.logging.JUtilLogger
import ru.astrainteractive.astralibs.logging.Logger

object RootModuleImpl : RootModule {
    override val plugin = Lateinit<AspeKt>()
    override val logger: Dependency<Logger> = Single {
        val plugin by plugin
        JUtilLogger("AspeKt", plugin.dataFolder)
    }
    override val eventListener: Dependency<EventListener> = Single {
        object : EventListener {}
    }
    override val dispatchers = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val scope: Dependency<AsyncComponent> = Single {
        object : AsyncComponent() {}
    }
    override val configFileManager = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, "config.yml")
    }
    override val pluginConfig = Reloadable {
        PluginConfiguration(configFileManager.value.fileConfiguration)
    }
    override val translation = Reloadable {
        val plugin by plugin
        PluginTranslation(plugin)
    }
    override val discordEvent = Single {
        Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@Single null
        Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@Single null
        val controllersModule by ControllersModuleImpl
        DiscordEvent(
            discordController = controllersModule.discordController,
            luckPermsController = controllersModule.luckPermsController
        )
    }
    override val autoBroadcastJob = Single {
        AutoBroadcastJob(
            config = pluginConfig,
            dispatchers = dispatchers.value,
            scope = scope.value
        )
    }
}
