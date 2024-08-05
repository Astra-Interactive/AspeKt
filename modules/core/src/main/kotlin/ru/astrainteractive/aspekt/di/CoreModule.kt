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
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.getValue

interface CoreModule : Lifecycle {
    val plugin: Lateinit<JavaPlugin>
    val eventListener: EventListener

    val dispatchers: BukkitDispatchers
    val scope: AsyncComponent
    val pluginConfig: Reloadable<PluginConfiguration>
    val translation: Reloadable<PluginTranslation>
    val yamlFormat: StringFormat

    val economyProvider: Reloadable<EconomyProvider?>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>
    val inventoryClickEventListener: DefaultInventoryClickEvent

    val jsonStringFormat: StringFormat

    class Default : CoreModule {

        // Core
        override val plugin = Lateinit<JavaPlugin>(true)

        override val eventListener by lazy {
            object : EventListener {} // todo DefaultEventListener
        }

        override val dispatchers by lazy {
            val plugin by plugin
            DefaultBukkitDispatchers(plugin)
        }

        override val scope by lazy {
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

        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
        }

        override val inventoryClickEventListener by lazy {
            DefaultInventoryClickEvent()
        }

        override val jsonStringFormat: StringFormat by lazy {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
            }
        }

        override fun onDisable() {
            inventoryClickEventListener.onDisable()
            eventListener.onDisable()
            scope.close()
        }

        override fun onEnable() {
            inventoryClickEventListener.onEnable(plugin.value)
            eventListener.onEnable(plugin.value)
            economyProvider.reload()
        }

        override fun onReload() {
            pluginConfig.reload()
            translation.reload()
            economyProvider.reload()
        }
    }
}
