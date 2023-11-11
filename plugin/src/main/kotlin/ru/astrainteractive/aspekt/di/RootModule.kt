package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
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
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {

    val plugin: Lateinit<AspeKt>
    val eventListener: Dependency<EventListener>

    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val logger: Dependency<Logger>
    val pluginConfig: Reloadable<PluginConfiguration>
    val adminChunksYml: Reloadable<FileManager>
    val translation: Reloadable<PluginTranslation>

    val menuModels: Reloadable<List<MenuModel>>
    val economyProvider: Reloadable<EconomyProvider?>
    val tempFileManager: Reloadable<SpigotFileManager>
    val translationContext: BukkitTranslationContext
    val inventoryClickEventListener: Single<DefaultInventoryClickEvent>

    val adminPrivateModule: AdminPrivateModule
    val eventsModule: EventsModule
    val guiModule: GuiModule
    val autoBroadcastModule: AutoBroadcastModule
    val commandManagerModule: CommandManagerModule
}
