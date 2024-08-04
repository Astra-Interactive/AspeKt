package ru.astrainteractive.aspekt.di

import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProviderFactory
import ru.astrainteractive.astralibs.event.EventListener
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
import java.io.File

interface CoreModule : Lifecycle {
    val plugin: Lateinit<JavaPlugin>
    val eventListener: Dependency<EventListener>

    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val pluginConfig: Reloadable<PluginConfiguration>
    val adminChunksYml: Reloadable<File>
    val translation: Reloadable<PluginTranslation>
    val yamlFormat: StringFormat

    val economyProvider: Reloadable<EconomyProvider?>
    val tempFile: File
    val tempFileConfiguration: Reloadable<FileConfiguration>
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

        override val yamlFormat: StringFormat by lazy {
            YamlStringFormat()
        }

        override val pluginConfig = Reloadable {
            val file = plugin.value.dataFolder.resolve("config.yml")
            val translation = yamlFormat.parseOrDefault(file, ::PluginConfiguration)
            val yamlString = yamlFormat.encodeToString(translation)
            file.writeText(yamlString)
            translation
        }

        override val adminChunksYml: Reloadable<File> = Reloadable {
            plugin.value.dataFolder.resolve("adminchunks.yml")
        }

        override val translation = Reloadable {
            val file = plugin.value.dataFolder.resolve("translations.yml")

            val translation = yamlFormat.parseOrDefault(file, ::PluginTranslation)
            val yamlString = yamlFormat.encodeToString(translation)
            file.writeText(yamlString)
            translation
        }

        override val economyProvider: Reloadable<EconomyProvider?> = Reloadable {
            kotlin.runCatching { EconomyProviderFactory(plugin.value).create() }.getOrNull()
        }

        override val tempFile by lazy {
            plugin.value.dataFolder.resolve("temp.yml")
        }
        override val tempFileConfiguration: Reloadable<FileConfiguration> = Reloadable {
            YamlConfiguration.loadConfiguration(tempFile)
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
            tempFileConfiguration.reload()
        }
    }
}
