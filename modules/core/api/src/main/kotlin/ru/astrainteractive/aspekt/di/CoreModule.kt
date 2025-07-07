package ru.astrainteractive.aspekt.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import ru.astrainteractive.aspekt.di.factory.ConfigKrateFactory
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astralibs.server.MinecraftNativeBridge
import ru.astrainteractive.astralibs.server.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface CoreModule {
    val dataFolder: File
    val lifecycle: Lifecycle

    val dispatchers: KotlinDispatchers

    val minecraftNativeBridge: MinecraftNativeBridge

    val scope: CoroutineScope
    val mainScope: CoroutineScope
    val pluginConfig: CachedKrate<PluginConfiguration>
    val translation: CachedKrate<PluginTranslation>
    val yamlFormat: StringFormat

    val kyoriComponentSerializer: CachedKrate<KyoriComponentSerializer>

    val jsonStringFormat: StringFormat
    val platformServer: PlatformServer

    class Default(
        override val dataFolder: File,
        override val dispatchers: KotlinDispatchers,
        override val minecraftNativeBridge: MinecraftNativeBridge,
        override val platformServer: PlatformServer
    ) : CoreModule, Logger by JUtiltLogger("CoreModule") {
        // Core

        override val scope = CoroutineFeature.Default(Dispatchers.IO)
        override val mainScope: CoroutineScope = CoroutineFeature.Default(dispatchers.Main)

        override val yamlFormat: StringFormat = YamlStringFormat(
            configuration = Yaml.default.configuration.copy(
                encodeDefaults = true,
                strictMode = false,
                polymorphismStyle = PolymorphismStyle.Property
            ),
        )

        override val pluginConfig = ConfigKrateFactory.fileConfigKrate(
            file = dataFolder.resolve("config.yml"),
            stringFormat = yamlFormat,
            factory = ::PluginConfiguration
        ).asCachedMutableKrate()

        override val translation = ConfigKrateFactory.fileConfigKrate(
            file = dataFolder.resolve("translations.yml"),
            stringFormat = yamlFormat,
            factory = ::PluginTranslation
        ).asCachedMutableKrate()

        override val kyoriComponentSerializer = DefaultMutableKrate<KyoriComponentSerializer>(
            loader = { null },
            factory = { KyoriComponentSerializer.Legacy }
        ).asCachedKrate()

        override val jsonStringFormat: StringFormat = Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {},
            onReload = {
                pluginConfig.getValue()
                translation.getValue()
            },
            onDisable = {
                scope.cancel()
            }
        )
    }
}
