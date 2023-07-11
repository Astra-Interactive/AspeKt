package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.events.discord.DiscordEvent
import ru.astrainteractive.aspekt.plugin.AutoBroadcastJob
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger

interface RootModule : Module {
    val plugin: Dependency<AspeKt>
    val logger: Dependency<Logger>
    val eventListener: Dependency<EventListener>
    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val configFileManager: Dependency<SpigotFileManager>
    val pluginConfig: Reloadable<PluginConfiguration>
    val translation: Reloadable<PluginTranslation>
    val discordEvent: Dependency<DiscordEvent?>
    val autoBroadcastJob: Dependency<AutoBroadcastJob>
}
