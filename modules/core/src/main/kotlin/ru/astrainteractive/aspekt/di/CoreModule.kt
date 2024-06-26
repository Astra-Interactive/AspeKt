package ru.astrainteractive.aspekt.di

import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProviderFactory
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultFileConfigurationManager
import ru.astrainteractive.astralibs.filemanager.FileConfigurationManager
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.astralibs.filemanager.impl.JVMFileManager
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface CoreModule : Lifecycle {
    val plugin: Lateinit<JavaPlugin>
    val eventListener: Dependency<EventListener>

    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val pluginConfig: Reloadable<PluginConfiguration>
    val adminChunksYml: Reloadable<FileManager>
    val translation: Reloadable<PluginTranslation>

    val economyProvider: Reloadable<EconomyProvider?>
    val tempFileManager: Reloadable<FileConfigurationManager>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>
    val inventoryClickEventListener: Single<DefaultInventoryClickEvent>

    val tempFileStringFormat: StringFormat

    class Default : CoreModule {

        // Core
        override val plugin = Lateinit<JavaPlugin>(true)

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
            val fileManager = DefaultFileConfigurationManager(plugin.value, "config.yml")
            val yamlSerializer = YamlStringFormat()

            val translation = yamlSerializer.parseOrDefault(fileManager.configFile, ::PluginConfiguration)
            val yamlString = yamlSerializer.yaml.encodeToString(translation)
            fileManager.configFile.writeText(yamlString)
            translation
        }

        override val adminChunksYml: Reloadable<FileManager> = Reloadable {
            JVMFileManager("adminchunks.yml", plugin.value.dataFolder)
        }

        override val translation = Reloadable {
            val fileManager = DefaultFileConfigurationManager(plugin.value, "translations.yml")
            val yamlSerializer = YamlStringFormat()

            val translation = yamlSerializer.parseOrDefault(fileManager.configFile, ::PluginTranslation)
            val yamlString = yamlSerializer.yaml.encodeToString(translation)
            fileManager.configFile.writeText(yamlString)
            translation
        }

        override val economyProvider: Reloadable<EconomyProvider?> = Reloadable {
            kotlin.runCatching { EconomyProviderFactory(plugin.value).create() }.getOrNull()
        }

        override val tempFileManager: Reloadable<FileConfigurationManager> = Reloadable {
            DefaultFileConfigurationManager(plugin.value, "temp.yml")
        }

        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
        }

        override val inventoryClickEventListener: Single<DefaultInventoryClickEvent> = Single {
            DefaultInventoryClickEvent()
        }

        override val tempFileStringFormat: StringFormat by lazy {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = false
            }
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
            economyProvider.reload()
            tempFileManager.reload()
        }
    }
}
