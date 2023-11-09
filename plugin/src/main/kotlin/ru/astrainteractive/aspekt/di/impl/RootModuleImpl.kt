package ru.astrainteractive.aspekt.di.impl

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.di.factories.MenuModelFactory
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.event.discord.DiscordEvent
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.gui.RouterImpl
import ru.astrainteractive.aspekt.plugin.AutoBroadcastJob
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.AnyEconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.filemanager.impl.JVMFileManager
import ru.astrainteractive.astralibs.logging.JUtilLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

class RootModuleImpl : RootModule {

    // Core
    override val plugin = Lateinit<AspeKt>(true)
    override val logger: Dependency<Logger> = Single {
        val plugin by plugin
        JUtilLogger("AspeKt", plugin.dataFolder)
    }
    override val eventListener: Dependency<EventListener> = Single {
        object : EventListener {} // todo DefaultEventListener
    }
    override val dispatchers = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val scope: Dependency<AsyncComponent> = Single {
        object : AsyncComponent() {} // todo DefaultAsyncComponent
    }
    override val configFileManager = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, "config.yml")
    }
    override val pluginConfig = Reloadable {
        PluginConfiguration(configFileManager.value.fileConfiguration)
    }
    override val adminChunksYml: Reloadable<FileManager> = Reloadable {
        JVMFileManager("adminchunks.yml", plugin.value.dataFolder)
    }
    override val translation = Reloadable {
        val plugin by plugin
        PluginTranslation()
    }
    override val menuModels: Reloadable<List<MenuModel>> = Reloadable {
        val dataFolder = plugin.value.dataFolder
        val menuFolder = File(dataFolder, "menu").also {
            if (!it.exists()) it.mkdirs()
        }
        menuFolder.listFiles()
            .orEmpty()
            .filterNotNull()
            .filter(File::exists)
            .filter(File::isFile)
            .mapNotNull {
                runCatching { MenuModelFactory(it).create() }
                    .onFailure { it.printStackTrace() }
                    .getOrNull()
            }
    }

    // Modules
    override val controllersModule: ControllersModule by Single {
        ControllersModuleImpl(this)
    }
    override val eventsModule: EventsModule by Single {
        EventsModuleImpl(this)
    }
    override val commandsModule: CommandsModule by Single {
        CommandsModuleImpl(this)
    }
    override val adminPrivateModule: AdminPrivateControllerDependencies by Single {
        AdminPrivateControllerDependencies.Default(
            adminChunksYml = { adminChunksYml.value },
            dispatchers = { dispatchers.value }
        )
    }
    override val economyProvider: Reloadable<EconomyProvider?> = Reloadable {
        runCatching {
            AnyEconomyProvider(plugin.value)
        }.onFailure { it.printStackTrace() }.getOrNull()
    }
    override val tempFileManager: Reloadable<SpigotFileManager> = Reloadable {
        DefaultSpigotFileManager(plugin.value, "temp.yml")
    }

    // etc
    override val discordEvent = Single {
        Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@Single null
        Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@Single null
        val discordEventModule = DiscordEventModuleImpl(this)
        DiscordEvent(discordEventModule)
    }
    override val autoBroadcastJob = Single {
        AutoBroadcastJob(
            config = pluginConfig,
            dispatchers = dispatchers.value,
            scope = scope.value
        )
    }
    override val translationContext: BukkitTranslationContext by Single {
        val serializer = KyoriComponentSerializer.Legacy
        BukkitTranslationContext.Default { serializer }
    }
    override val inventoryClickEventListener: Single<DefaultInventoryClickEvent> = Single {
        DefaultInventoryClickEvent()
    }
    override val router: Provider<Router> = Provider {
        RouterImpl(
            scope = scope.value,
            dispatchers = dispatchers.value,
            translationContext = translationContext,
            economyProvider = economyProvider.value,
            translation = translation.value
        )
    }
}
