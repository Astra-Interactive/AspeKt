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
import ru.astrainteractive.astralibs.util.fileConfigKrate
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface CoreModule {
    val dataFolder: File
    val lifecycle: Lifecycle

    val dispatchers: KotlinDispatchers

    val scope: CoroutineScope
    val pluginConfig: Krate<PluginConfiguration>
    val translation: Krate<PluginTranslation>
    val yamlFormat: StringFormat

    val kyoriComponentSerializer: Krate<KyoriComponentSerializer>

    val jsonStringFormat: StringFormat

    class Default(
        override val dataFolder: File,
        override val dispatchers: KotlinDispatchers,
    ) : CoreModule, Logger by JUtiltLogger("CoreModule") {
        // Core

        override val scope = CoroutineFeature.Default(Dispatchers.IO)

        override val yamlFormat: StringFormat = YamlStringFormat(
            configuration = Yaml.default.configuration.copy(
                encodeDefaults = true,
                strictMode = false,
                polymorphismStyle = PolymorphismStyle.Property
            ),
        )

        override val pluginConfig = fileConfigKrate(
            file = dataFolder.resolve("config.yml"),
            stringFormat = yamlFormat,
            factory = ::PluginConfiguration
        )

        override val translation = ConfigKrateFactory.create(
            fileNameWithoutExtension = "translations",
            stringFormat = yamlFormat,
            dataFolder = dataFolder,
            factory = ::PluginTranslation
        )

        override val kyoriComponentSerializer = DefaultMutableKrate<KyoriComponentSerializer>(
            loader = { null },
            factory = { KyoriComponentSerializer.Legacy }
        )

        override val jsonStringFormat: StringFormat = Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {},
            onReload = {
                pluginConfig.loadAndGet()
                translation.loadAndGet()
            },
            onDisable = {
                scope.cancel()
            }
        )
    }
}
