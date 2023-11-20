package ru.astrainteractive.aspekt.di

import kotlinx.serialization.encodeToString
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.factory.MenuModelFactory
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.Lifecycle
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
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
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

interface CoreModule : Lifecycle {
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

    class Default : CoreModule {

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
            AsyncComponent.Default()
        }

        override val pluginConfig = Reloadable {
            val configFileManager = DefaultSpigotFileManager(plugin.value, "config.yml")
            PluginConfiguration(configFileManager.fileConfiguration)
        }

        override val adminChunksYml: Reloadable<FileManager> = Reloadable {
            JVMFileManager("adminchunks.yml", plugin.value.dataFolder)
        }

        override val translation = Reloadable {
            val file = DefaultSpigotFileManager(plugin.value, "translations.yml")
            val translation = YamlSerializer().safeParse<PluginTranslation>(file.configFile)
                .getOrNull() ?: PluginTranslation()
            val yamlString = YamlSerializer().yaml.encodeToString(translation)
            file.configFile.writeText(yamlString)
            translation
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

        override val economyProvider: Reloadable<EconomyProvider?> = Reloadable {
            runCatching {
                AnyEconomyProvider(plugin.value)
            }.onFailure { it.printStackTrace() }.getOrNull()
        }

        override val tempFileManager: Reloadable<SpigotFileManager> = Reloadable {
            DefaultSpigotFileManager(plugin.value, "temp.yml")
        }

        override val translationContext: BukkitTranslationContext by Single {
            val serializer = KyoriComponentSerializer.Legacy
            BukkitTranslationContext.Default { serializer }
        }

        override val inventoryClickEventListener: Single<DefaultInventoryClickEvent> = Single {
            DefaultInventoryClickEvent()
        }

        override fun onDisable() {
            inventoryClickEventListener.value.onDisable()
            eventListener.value.onDisable()
            scope.value.close()
        }

        override fun onEnable() {
            inventoryClickEventListener.value.onEnable(plugin.value)
            eventListener.value.onEnable(plugin.value)
            economyProvider.reload()
        }

        override fun onReload() {
            pluginConfig.reload()
            translation.reload()
            menuModels.reload()
            economyProvider.reload()
            tempFileManager.reload()
        }
    }
}
