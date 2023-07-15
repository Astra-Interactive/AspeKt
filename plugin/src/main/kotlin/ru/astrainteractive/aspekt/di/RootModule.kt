package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.event.discord.DiscordEvent
import ru.astrainteractive.aspekt.plugin.AutoBroadcastJob
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Reloadable

interface RootModule : Module {
    val plugin: Lateinit<AspeKt>
    val logger: Dependency<Logger>
    val eventListener: Dependency<EventListener>
    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val configFileManager: Dependency<SpigotFileManager>
    val pluginConfig: Reloadable<PluginConfiguration>
    val translation: Reloadable<PluginTranslation>
    val discordEvent: Dependency<DiscordEvent?>
    val autoBroadcastJob: Dependency<AutoBroadcastJob>

    val controllersModule: ControllersModule
    val eventsModule: EventsModule
    val commandsModule: CommandsModule
}
