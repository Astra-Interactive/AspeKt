package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.di.AdminPrivateControllerModule
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.event.discord.DiscordEvent
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.plugin.AutoBroadcastJob
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {
    val plugin: Lateinit<AspeKt>
    val logger: Dependency<Logger>
    val eventListener: Dependency<EventListener>
    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val configFileManager: Dependency<SpigotFileManager>
    val pluginConfig: Reloadable<PluginConfiguration>
    val adminChunksYml: Reloadable<FileManager>
    val translation: Reloadable<PluginTranslation>
    val discordEvent: Dependency<DiscordEvent?>
    val autoBroadcastJob: Dependency<AutoBroadcastJob>
    val menuModels: Reloadable<List<MenuModel>>
    val controllersModule: ControllersModule
    val eventsModule: EventsModule
    val commandsModule: CommandsModule
    val adminPrivateModule: AdminPrivateControllerModule
    val economyProvider: Reloadable<EconomyProvider?>
    val tempFileManager: Reloadable<SpigotFileManager>
    val translationContext: BukkitTranslationContext
    val inventoryClickEventListener: Single<DefaultInventoryClickEvent>
    val router: Provider<Router>
}
